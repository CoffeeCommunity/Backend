package coffee.community.backend.auth.dto;

public record AccessTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}