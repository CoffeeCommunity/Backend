package coffee.community.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter  // JSON 바인딩용
public class UserUpdateRequest {

    @NotBlank(message = "{userUpdateRequest.nickname.required}")
    @Size(max = 50, message = "{userUpdateRequest.nickname.max}")
    private String nickname;

    @Size(max = 20, message = "{userUpdateRequest.phoneNumber.max}")
    private String phoneNumber;

    @Size(max = 255, message = "{userUpdateRequest.bio.max}")
    private String bio;

    @Size(max = 500, message = "{userUpdateRequest.profileImageUrl.max}")
    private String profileImageUrl;
}