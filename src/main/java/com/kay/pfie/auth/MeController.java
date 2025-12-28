package com.kay.pfie.auth;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class MeController {

    public record MeResponse(java.util.UUID id, String email) {}

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        var p = (AuthPrincipal) authentication.getPrincipal();
        return new MeResponse(p.userId(), p.email());
    }
}