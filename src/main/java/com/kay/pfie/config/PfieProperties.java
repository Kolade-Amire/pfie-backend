package com.kay.pfie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pfie")
public record PfieProperties(Auth auth, UserDefaults userDefaults) {
    public record Auth(Jwt jwt, Google google) {
        public record Jwt(String issuer, String secret) {}
        public record Google(String clientId) {}
    }
    public record UserDefaults(String currency, String timeZone) {}
}
