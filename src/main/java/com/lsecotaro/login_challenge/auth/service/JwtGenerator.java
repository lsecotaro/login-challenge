package com.lsecotaro.login_challenge.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtGenerator {
    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";
    private static final long ONE_MIN_IN_MILLIS = 60 * 1000 * 60;
    private static final long EXPIRATION_TIME = 10 * ONE_MIN_IN_MILLIS;

    public String generateToken(String user, Map<String, Object> claims) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
