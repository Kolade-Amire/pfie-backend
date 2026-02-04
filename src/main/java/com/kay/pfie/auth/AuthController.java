package com.kay.pfie.auth;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.kay.pfie.common.web.ApiPaths;
import com.kay.pfie.auth.AuthDtos.AuthResponse;
import com.kay.pfie.auth.AuthDtos.GoogleAuthRequest;
import com.kay.pfie.auth.AuthDtos.UserDto;

@RestController
@RequestMapping(ApiPaths.V1 + "/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/google")
    public AuthResponse google(@RequestBody GoogleAuthRequest req) {
        var r = authService.loginWithGoogle(req.idToken());
        return new AuthResponse(
                r.accessToken(),
                new UserDto(r.userId(), r.email(), r.displayName(), r.avatarUrl())
        );
    }
}
