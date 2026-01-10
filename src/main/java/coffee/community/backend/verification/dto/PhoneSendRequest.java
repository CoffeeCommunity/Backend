package coffee.community.backend.verification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PhoneSendRequest {

    @NotBlank
    @Pattern(regexp = "^01[016789]\\d{7,8}$")
    private String phoneNumber;
}
