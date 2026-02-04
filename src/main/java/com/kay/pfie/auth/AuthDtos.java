package com.kay.pfie.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record GoogleAuthRequest(@NotBlank String idToken) {}
    public record AuthResponse(String accessToken, UserDto user) {}
    public record UserDto(java.util.UUID id, String email, String displayName, String avatarUrl) {}
    public record AuthResult(String accessToken, java.util.UUID userId, String email, String displayName, String avatarUrl) {}
}
