package coffee.community.backend.auth.dto;

import coffee.community.backend.user.dto.UserResponse;

public record LoginResponse(
        UserResponse user,
        String accessToken,
        String refreshToken,
        long expiresIn
) {}