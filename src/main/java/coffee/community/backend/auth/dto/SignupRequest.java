package coffee.community.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class SignupRequest {

    @Email(message = "{signupRequest.email.invalid}")
    @NotBlank(message = "{signupRequest.email.required}")
    @Size(max = 255, message = "{signupRequest.email.max}")
    private String email;

    @NotBlank(message = "{signupRequest.password.required}")
    @Size(min = 8, max = 100, message = "{signupRequest.password.size}")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,100}$",
            message = "{signupRequest.password.pattern}")
    private String password;

    @NotBlank(message = "{signupRequest.nickname.required}")
    @Size(min = 2, max = 10, message = "{signupRequest.nickname.size}")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
            message = "{signupRequest.nickname.pattern}")
    private String nickname;

    @NotBlank(message = "{signupRequest.phoneNumber.required}")
    @Pattern(regexp = "^01[016789]\\d{7,8}$",
            message = "{signupRequest.phoneNumber.pattern}")
    private String phoneNumber;

    @NotBlank(message = "{signupRequest.verificationToken.required}")
    @Size(min = 36, max = 36, message = "{signupRequest.verificationToken.size}")
    private String verificationToken;

    @URL(message = "{signupRequest.profileImageUrl.invalid}")
    @Size(max = 500, message = "{signupRequest.profileImageUrl.max}")
    private String profileImageUrl;

    @Size(max = 500, message = "{signupRequest.bio.max}")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s.,!?@#$%^&*()_+-=]{0,500}$",
            message = "{signupRequest.bio.pattern}")
    private String bio;
}