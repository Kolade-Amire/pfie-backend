package com.kay.pfie.auth;

import com.kay.pfie.config.PfieProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthWiring {

    @Bean
    public GoogleIdTokenService googleIdTokenService(PfieProperties props) {
        String clientId = props.auth().google().clientId();
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("GOOGLE_CLIENT_ID is required");
        }
        return new GoogleIdTokenService(clientId);
    }

    @Bean
    public JwtService jwtService(PfieProperties props) {
        return new JwtService(props.auth().jwt().issuer(), props.auth().jwt().secret());
    }
}