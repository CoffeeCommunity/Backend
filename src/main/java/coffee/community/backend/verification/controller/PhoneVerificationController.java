package coffee.community.backend.verification.controller;

import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.verification.dto.PhoneSendRequest;
import coffee.community.backend.verification.dto.PhoneVerifyRequest;
import coffee.community.backend.verification.dto.VerificationTokenResponse;
import coffee.community.backend.verification.service.PhoneVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/phone")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    /**
     * 인증번호 발송
     */
    @PostMapping("/send")
    public ApiResponse<String> sendCode(@RequestBody @Valid PhoneSendRequest request) {
        boolean success = phoneVerificationService.sendCode(request.getPhoneNumber());
        if (!success) {
            return ApiResponse.error("인증번호 발송 실패");
        }
        return ApiResponse.ok("인증번호가 발송되었습니다.");
    }

    /**
     * 인증번호 검증 → verificationToken 발급
     */
    @PostMapping("/verify")
    public ApiResponse<VerificationTokenResponse> verifyCode(@RequestBody @Valid PhoneVerifyRequest request) {
        boolean verified = phoneVerificationService.verifyCode(request.getPhoneNumber(), request.getCode());
        if (!verified) {
            return ApiResponse.error("인증번호 불일치");
        }
        String token = phoneVerificationService.issueToken(request.getPhoneNumber());
        return ApiResponse.ok(new VerificationTokenResponse(token));
    }
}