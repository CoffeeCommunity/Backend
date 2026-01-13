package coffee.community.backend.auth.dto;

import coffee.community.backend.user.dto.UserResponse;

public record LoginResult(
        UserResponse user,
        String accessToken,
        String refreshToken,
        long expiresIn,
        long refreshTokenExpiresIn
) {
}