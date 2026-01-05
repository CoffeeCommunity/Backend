package coffee.community.backend.auth.controller;

import coffee.community.backend.auth.dto.*;
import coffee.community.backend.auth.service.AuthService;
import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 회원가입 */
    @PostMapping("/signup")
    public ApiResponse<Void> signup(
            @RequestBody @Valid SignupRequest request
    ) {
        authService.signup(request);
        return ApiResponse.ok();
    }

    /** 일반 로그인 */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        return ApiResponse.ok(authService.login(request));
    }

    /** OAuth 로그인 */
    @PostMapping("/oauth/login")
    public ApiResponse<TokenResponse> oauthLogin(
            @RequestBody @Valid OAuthLoginRequest request
    ) {
        return ApiResponse.ok(authService.oauthLogin(request));
    }

    /** 회원 탈퇴 */
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMe() {
        authService.deleteMe(SecurityUtil.getCurrentUserId());
        return ApiResponse.ok();
    }
}