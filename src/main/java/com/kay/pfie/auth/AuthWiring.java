package com.kay.pfie.auth;

import com.kay.pfie.config.PfieGoogleAuthProperties;
import com.kay.pfie.config.PfieJwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthWiring {

    @Bean
    public GoogleIdTokenService googleIdTokenService(PfieGoogleAuthProperties props) {
        return new GoogleIdTokenService(props.getClientId());
    }

    @Bean
    public JwtService jwtService(PfieJwtProperties props) {
        return new JwtService(props.getIssuer(), props.getSecret());
    }
}
