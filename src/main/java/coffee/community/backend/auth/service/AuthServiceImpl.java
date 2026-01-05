package coffee.community.backend.auth.service;

import coffee.community.backend.auth.dto.*;
import coffee.community.backend.auth.exception.AuthErrorCode;
import coffee.community.backend.auth.exception.AuthException;
import coffee.community.backend.global.security.JwtTokenProvider;
import coffee.community.backend.user.entity.User;
import coffee.community.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public void signup(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // ❗ 휴대폰 인증을 필수로 만들고 싶으면 여기서 검증
//         if (!verificationService.isVerified(request.getVerificationToken())) {
//            throw new AuthException(AuthErrorCode.PHONE_NOT_VERIFIED);
//        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .deleted(false)
                .build();

        userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new AuthException(AuthErrorCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        return new TokenResponse(jwtTokenProvider.createAccessToken(user.getId()));
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
                                        .deleted(false)
                                        .build()
                        )
                );

        return new TokenResponse(jwtTokenProvider.createAccessToken(user.getId()));
    }

    @Override
    @Transactional
    public void deleteMe(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        user.delete(); // soft delete
    }
}