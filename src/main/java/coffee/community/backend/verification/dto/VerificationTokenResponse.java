package coffee.community.backend.verification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerificationTokenResponse {

    private String verificationToken;
}