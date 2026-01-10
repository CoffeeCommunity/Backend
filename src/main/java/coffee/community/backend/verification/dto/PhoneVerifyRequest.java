package coffee.community.backend.verification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PhoneVerifyRequest {
    @NotBlank
    @Pattern(regexp = "^01[016789]\\d{7,8}$")  // phoneNumber에도 통일
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$")  // 6자리 숫자 (generateCode와 맞춤)
    private String code;
}