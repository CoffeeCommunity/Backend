package coffee.community.backend.auth.controller;

import coffee.community.backend.auth.dto.*;
import coffee.community.backend.auth.exception.AuthErrorCode;
import coffee.community.backend.auth.exception.AuthException;
import coffee.community.backend.auth.service.AuthService;
import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.util.SecurityUtil;
import coffee.community.backend.user.entity.User;
import coffee.community.backend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    /** 회원가입 */
    @PostMapping("/signup")
    public ApiResponse<Long> signup(@Valid @RequestBody SignupRequest request) {
        Long userId = authService.signup(request);  // 사용!
        return ApiResponse.ok(userId, "회원가입이 완료되었습니다.");
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
    public ApiResponse<String> deleteMe(
            Authentication authentication,  // SecurityContext
            @AuthenticationPrincipal String email) {  // 또는 email 추출

        // 1. email → User 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED));

        // 2. 본인 확인 (보안)
        String requestEmail = authentication.getName();  // SecurityContext에서 email
        if (!requestEmail.equals(user.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, HttpStatus.FORBIDDEN);
        }

        // 3. 탈퇴
        boolean deleted = authService.deleteMe(user.getId());
        if (deleted) {
            return ApiResponse.ok("회원 탈퇴 완료");
        }
        return ApiResponse.ok("이미 탈퇴 처리된 계정입니다");
    }
}