package coffee.community.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter  // 체이닝을 위한 setter 추가 (JSON 바인딩용)
public class UserUpdateRequest {

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 50, message = "닉네임은 50자 이하여야 합니다")
    private String nickname;

    @Size(max = 255, message = "자기소개는 255자 이하여야 합니다")
    private String bio;

    @Size(max = 500, message = "프로필 이미지는 500자 이하여야 합니다")
    private String profileImageUrl;
}