package com.gradems.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long userId,
        String username,
        String fullName,
        String role
) {
    public static AuthResponse of(String accessToken, String refreshToken,
                                   Long userId, String username, String fullName, String role) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", userId, username, fullName, role);
    }
}
