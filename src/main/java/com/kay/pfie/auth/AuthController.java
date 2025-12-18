package com.kay.pfie.auth;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record GoogleAuthRequest(@NotBlank String idToken) {}
    public record AuthResponse(String accessToken, UserDto user) {}
    public record UserDto(java.util.UUID id, String email, String displayName, String avatarUrl) {}

    @PostMapping("/google")
    public AuthResponse google(@RequestBody GoogleAuthRequest req) {
        var r = authService.loginWithGoogle(req.idToken());
        return new AuthResponse(
                r.accessToken(),
                new UserDto(r.userId(), r.email(), r.displayName(), r.avatarUrl())
        );
    }
}
