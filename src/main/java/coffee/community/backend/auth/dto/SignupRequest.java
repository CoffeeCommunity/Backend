package coffee.community.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^01[016789]\\d{7,8}$")
    private String phoneNumber;

    @NotBlank
    private String verificationToken;
}