package coffee.community.backend.auth.controller;

import coffee.community.backend.auth.dto.*;
import coffee.community.backend.auth.exception.AuthErrorCode;
import coffee.community.backend.auth.exception.AuthException;
import coffee.community.backend.auth.service.AuthService;
import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.security.JwtTokenProvider;
import coffee.community.backend.user.entity.User;
import coffee.community.backend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /** 회원가입 */
    @PostMapping("/signup")
    public ApiResponse<Long> signup(@Valid @RequestBody SignupRequest request) {
        Long userId = authService.signup(request);

        return ApiResponse.ok(
                userId,
                messageSource.getMessage(
                        "auth.signup.success",
                        null,
                        LocaleContextHolder.getLocale()
                )
        );
    }

    /** 일반 로그인 */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ApiResponse.ok(
                response,
                messageSource.getMessage(
                        "auth.login.success",
                        null,
                        LocaleContextHolder.getLocale()
                )
        );
    }

    /** OAuth 로그인 */
    @PostMapping("/oauth/login")
    public ApiResponse<LoginResponse> oauthLogin(
            @Valid @RequestBody OAuthLoginRequest request
    ) {
        LoginResponse response = authService.oauthLogin(request);

        return ApiResponse.ok(
                response,
                messageSource.getMessage(
                        "auth.login.success",
                        null,
                        LocaleContextHolder.getLocale()
                )
        );
    }

    /** 회원 탈퇴 */
    @DeleteMapping("/me")
    public ApiResponse<String> deleteMe(
            Authentication authentication,
            @AuthenticationPrincipal String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AuthException(AuthErrorCode.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED)
                );

        String requestEmail = authentication.getName();
        if (!requestEmail.equals(user.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, HttpStatus.FORBIDDEN);
        }

        boolean deleted = authService.deleteMe(user.getId());

        if (deleted) {
            return ApiResponse.ok(
                    messageSource.getMessage(
                            "auth.user.delete.success",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        return ApiResponse.ok(
                messageSource.getMessage(
                        "auth.user.delete.already",
                        null,
                        LocaleContextHolder.getLocale()
                )
        );
    }
}