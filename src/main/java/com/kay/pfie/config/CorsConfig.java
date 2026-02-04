package com.kay.pfie.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(PfieCorsProperties props, Environment environment) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(resolveOrigins(props.getAllowedOrigins(), environment));
        config.setAllowedMethods(props.getAllowedMethods());
        config.setAllowedHeaders(props.getAllowedHeaders());
        config.setAllowCredentials(props.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private List<String> resolveOrigins(List<String> origins, Environment environment) {
        boolean isProd = environment.matchesProfiles("prod");
        if (origins == null || origins.isEmpty()) {
            if (isProd) {
                throw new IllegalStateException("pfie.cors.allowed-origins must be set in prod");
            }
            return List.of("http://localhost:3000", "http://localhost:5173");
        }
        return origins;
    }
}
