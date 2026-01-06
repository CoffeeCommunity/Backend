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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(
                    AuthErrorCode.EMAIL_ALREADY_EXISTS,
                    HttpStatus.CONFLICT
            );
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AuthException(
                    AuthErrorCode.PHONE_ALREADY_EXISTS,
                    HttpStatus.CONFLICT
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
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
                                        .nickname(
                                                request.getNickname() != null
                                                        ? request.getNickname()
                                                        : "user"
                                        )
                                        .password("OAUTH")
                                        .role(Role.USER)
                                        .deleted(false)
                                        .build()
                        )
                );

        return new TokenResponse(
                jwtTokenProvider.createAccessToken(user.getId())
        );
    }

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
            return false; // 멱등성
        }

        user.delete();
        return true;
    }
}