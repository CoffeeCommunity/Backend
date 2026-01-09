package coffee.community.backend.auth.service;

import coffee.community.backend.auth.dto.*;
import coffee.community.backend.auth.exception.AuthErrorCode;
import coffee.community.backend.auth.exception.AuthException;
import coffee.community.backend.global.security.JwtTokenProvider;
import coffee.community.backend.user.entity.Role;
import coffee.community.backend.user.entity.User;
import coffee.community.backend.user.repository.UserRepository;
import coffee.community.backend.verification.service.PhoneVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhoneVerificationService phoneVerificationService;

    @Override
    @Transactional
    public Long signup(SignupRequest request) {
        // 1. phone 토큰 검증
        boolean verified = phoneVerificationService.verifyToken(
                request.getPhoneNumber(), request.getVerificationToken());
        if (!verified) {
            throw new AuthException(AuthErrorCode.PHONE_NOT_VERIFIED, HttpStatus.BAD_REQUEST);
        }

        // 2. 3중 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (userRepository.existsByNickname(request.getNickname())) {  // 추가!
            throw new AuthException(AuthErrorCode.NICKNAME_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AuthException(AuthErrorCode.PHONE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        // 3. User 생성 (profileImageUrl 추가)
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(request.getProfileImageUrl())  // 추가!
                .role(Role.USER)
                .deleted(false)
                .build();

        return userRepository.save(user).getId();
    }

    @Override
    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthException(
                                AuthErrorCode.USER_NOT_FOUND,
                                HttpStatus.UNAUTHORIZED
                        )
                );

        if (user.isDeleted()) {
            throw new AuthException(
                    AuthErrorCode.USER_NOT_FOUND,
                    HttpStatus.UNAUTHORIZED
            );
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException(
                    AuthErrorCode.INVALID_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED
            );
        }

        return new TokenResponse(
                jwtTokenProvider.createAccessToken(user.getId())
        );
    }

    @Override
    @Transactional
    public TokenResponse oauthLogin(OAuthLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() ->
                        userRepository.save(
                                User.builder()
                                        .email(request.getEmail())
                                        .nickname(request.getNickname() != null ? request.getNickname() : "user")
                                        .profileImageUrl(request.getProfileImageUrl())  // null OK
                                        .password("OAUTH")
                                        .role(Role.USER)
                                        .deleted(false)
                                        .build()
                        )
                );
        return new TokenResponse(jwtTokenProvider.createAccessToken(user.getId()));
    }

    @Override
    @Transactional
    public boolean deleteMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED));

        if (user.isDeleted()) {
            log.info("이미 삭제된 사용자 {} (멱등성)", userId);
            return false;  // 반환 사용!
        }

        User deletedUser = user.delete();  // 반환값 사용!
        log.info("사용자 {} 삭제됨", deletedUser.getId());
        return true;
    }
}