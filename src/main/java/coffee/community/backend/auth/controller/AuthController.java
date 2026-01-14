package coffee.community.backend.auth.controller;

import coffee.community.backend.auth.dto.*;
import coffee.community.backend.auth.exception.AuthErrorCode;
import coffee.community.backend.auth.exception.AuthException;
import coffee.community.backend.auth.service.AuthService;
import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.security.JwtTokenProvider;
import coffee.community.backend.user.entity.User;
import coffee.community.backend.user.repository.UserRepository;
import coffee.community.backend.auth.dto.AccessTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse
    ) {
        LoginResult result = authService.login(request);

        // ✅ refreshToken → HttpOnly Cookie
        ResponseCookie refreshCookie =
                ResponseCookie.from("refreshToken", result.refreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/auth/refresh")
                        .maxAge(result.refreshTokenExpiresIn())
                        .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // ✅ accessToken만 JSON 응답
        return ApiResponse.ok(
                new LoginResponse(
                        result.user(),
                        result.accessToken(),
                        result.refreshToken(),
                        result.expiresIn()
                ),
                messageSource.getMessage(
                        "auth.login.success",
                        null,
                        LocaleContextHolder.getLocale()
                )
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<AccessTokenResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        AccessTokenResponse response =
                authService.refreshAccessToken(refreshToken);

        return ApiResponse.ok(
                response,
                messageSource.getMessage(
                        "auth.token.refresh.success",
                        null,
                        LocaleContextHolder.getLocale()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        boolean success = false;
        if (refreshToken != null) {
            success = authService.logout(refreshToken);
        }

        // 쿠키 삭제 (성공 여부와 무관)
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/auth/refresh")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        // i18n 메시지 success에 따라 동적
        String msgKey = success ? "auth.logout.success" : "auth.logout.already";
        String successMsg = messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());

        return ResponseEntity.ok(ApiResponse.ok(successMsg, null));
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