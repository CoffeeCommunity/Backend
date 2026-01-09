package coffee.community.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class SignupRequest {

    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "유효한 이메일 형식(@example.com)을 입력하세요")
    @NotBlank(message = "이메일을 입력하세요")
    @Size(max = 255, message = "이메일은 255자 이하여야 합니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Size(min = 8, max = 100, message = "비밀번호는 8~100자 입니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,100}$",
            message = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*) 조합 8자 이상")
    private String password;

    @NotBlank(message = "닉네임을 입력하세요")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자입니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
            message = "닉네임은 한글/영문/숫자만 가능합니다")
    private String nickname;

    @NotBlank(message = "전화번호를 입력하세요")
    @Pattern(regexp = "^01[016789]\\d{7,8}$",
            message = "올바른 휴대폰 번호 형식을 입력하세요 (010-1234-5678)")
    private String phoneNumber;

    @NotBlank(message = "인증 토큰을 입력하세요")
    @Size(min = 36, max = 36, message = "UUID 토큰 형식이 아닙니다")  // UUID 길이
    private String verificationToken;

    @URL(message = "유효한 이미지 URL을 입력하세요")
    @Size(max = 500, message = "프로필 이미지 URL은 500자 이하여야 합니다")
    private String profileImageUrl;

    @Size(max = 500, message = "자기소개는 500자 이하여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s.,!?@#$%^&*()_+-=]{0,500}$",
            message = "자기소개는 특수문자 제한 내에서 입력하세요")
    private String bio;
}