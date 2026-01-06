package coffee.community.backend.verification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PhoneVerifyRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String code;
}
