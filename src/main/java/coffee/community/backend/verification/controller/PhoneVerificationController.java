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
    public ApiResponse<Void> sendCode(
            @RequestBody @Valid PhoneSendRequest request
    ) {
        phoneVerificationService.sendCode(request.getPhoneNumber());
        return ApiResponse.ok();
    }

    /**
     * 인증번호 검증 → verificationToken 발급
     */
    @PostMapping("/verify")
    public ApiResponse<VerificationTokenResponse> verifyCode(
            @RequestBody @Valid PhoneVerifyRequest request
    ) {
        phoneVerificationService.verifyCode(
                request.getPhoneNumber(),
                request.getCode()
        );

        // token 발급
        String token = phoneVerificationService.issueToken(request.getPhoneNumber());

        return ApiResponse.ok(new VerificationTokenResponse(token));
    }
}