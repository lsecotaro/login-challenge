package com.lsecotaro.login_challenge.config;

import com.lsecotaro.login_challenge.auth.filter.AuthenticationFilter;
import com.lsecotaro.login_challenge.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class JwtConfig {

    @Bean
    JwtService jwtService(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-in-minutes}") Integer expirationInMinutes) {
        return new JwtService(secretKey, expirationInMinutes);
    }

    @Bean
    public AuthenticationFilter authenticationFilter(JwtService jwtService) {
        return new AuthenticationFilter(jwtService, publicUrls());
    }

    private Set<String> publicUrls() {
        return Set.of("public", "h2-console", "swagger-ui", "api-docs");
    }
}
