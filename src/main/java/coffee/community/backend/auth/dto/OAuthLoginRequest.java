package coffee.community.backend.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class OAuthLoginRequest {

    @Email
    private String email;

    private String nickname;

    private String profileImageUrl;
}