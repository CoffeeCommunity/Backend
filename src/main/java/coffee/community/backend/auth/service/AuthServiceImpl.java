package coffee.community.backend.auth.service;

import coffee.community.backend.auth.dto.*;
import coffee.community.backend.auth.exception.AuthErrorCode;
import coffee.community.backend.auth.exception.AuthException;
import coffee.community.backend.auth.repository.RefreshTokenRepository;
import coffee.community.backend.global.security.JwtTokenProvider;
import coffee.community.backend.user.dto.UserResponse;
import coffee.community.backend.user.entity.Role;
import coffee.community.backend.user.entity.User;
import coffee.community.backend.user.repository.UserRepository;
import coffee.community.backend.verification.service.PhoneVerificationService;
import coffee.community.backend.auth.dto.AccessTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhoneVerificationService phoneVerificationService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.user.default-profile-image-url}")
    private String defaultProfileImageUrl;

    /* ================= 공통 로그인 응답 ================= */

    private LoginResponse buildLoginResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return new LoginResponse(
                UserResponse.from(user),
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpireSeconds()
        );
    }

    /* ================= 회원가입 ================= */

    @Override
    @Transactional
    public Long signup(SignupRequest request) {

        // 1. 휴대폰 인증 검증
        boolean verified = phoneVerificationService.verifyToken(
                request.getPhoneNumber(),
                request.getVerificationToken()
        );

        if (!verified) {
            throw new AuthException(
                    AuthErrorCode.PHONE_NOT_VERIFIED,
                    HttpStatus.BAD_REQUEST
            );
        }

        // 2. 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new AuthException(AuthErrorCode.NICKNAME_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AuthException(AuthErrorCode.PHONE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        // 3. 프로필 이미지 처리
        String profileImageUrl = request.getProfileImageUrl();
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            profileImageUrl = defaultProfileImageUrl;
        }

        // 4. 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(profileImageUrl)
                .role(Role.USER)
                .deleted(false)
                .build();

        return userRepository.save(user).getId();
    }

    /* ================= 로그인 ================= */

    @Override
    public LoginResult login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthException(
                                AuthErrorCode.USER_NOT_FOUND,
                                HttpStatus.UNAUTHORIZED
                        )
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException(
                    AuthErrorCode.INVALID_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED
            );
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return new LoginResult(
                UserResponse.from(user),
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpireSeconds(),
                jwtTokenProvider.getRefreshTokenExpireSeconds()
        );
    }

    /* ================= OAuth 로그인 ================= */

    @Override
    @Transactional
    public LoginResponse oauthLogin(OAuthLoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {

                    String profileImageUrl = request.getProfileImageUrl();
                    if (profileImageUrl == null || profileImageUrl.isBlank()) {
                        profileImageUrl = defaultProfileImageUrl;
                    }

                    return userRepository.save(
                            User.builder()
                                    .email(request.getEmail())
                                    .nickname(
                                            request.getNickname() != null
                                                    ? request.getNickname()
                                                    : "user"
                                    )
                                    .profileImageUrl(profileImageUrl)
                                    .password("OAUTH")
                                    .role(Role.USER)
                                    .deleted(false)
                                    .build()
                    );
                });

        return buildLoginResponse(user);
    }

    /* ================= 회원 탈퇴 ================= */

    @Override
    @Transactional
    public boolean deleteMe(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new AuthException(
                                AuthErrorCode.USER_NOT_FOUND,
                                HttpStatus.UNAUTHORIZED
                        )
                );

        if (user.isDeleted()) {
            log.info("이미 삭제된 사용자 {} (멱등성)", userId);
            return false;
        }

        long deletedTokens =
                refreshTokenRepository.deleteAllByUserId(userId);

        log.info("사용자 {}의 refreshToken {}개 삭제", userId, deletedTokens);

        userRepository.save(user.delete());
        return true;
    }

    /* ================= AccessToken 재발급 ================= */

    @Override
    @Transactional
    public AccessTokenResponse refreshAccessToken(String refreshToken) {

        // 1. RefreshToken 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new AuthException(
                    AuthErrorCode.INVALID_REFRESH_TOKEN,
                    HttpStatus.UNAUTHORIZED
            );
        }

        Long userId =
                jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);

        // 2. AccessToken 재발급
        String newAccessToken =
                jwtTokenProvider.createAccessToken(userId);

        return new AccessTokenResponse(
                newAccessToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpireSeconds()
        );
    }

    @Override
    @Transactional
    public boolean logout(String refreshToken) {

        long deleted = refreshTokenRepository.deleteByToken(refreshToken);

        if (deleted > 0) {
            log.info("로그아웃 성공 (삭제된 refreshToken 수: {})", deleted);
            return true;
        }

        log.info("로그아웃 요청 - 이미 로그아웃된 토큰");
        return false;
    }
}