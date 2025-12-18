package com.kay.pfie.auth;

import java.util.UUID;

public record AuthPrincipal(UUID userId, String email) {}
