package com.lsecotaro.login_challenge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtService {
    private static final long ONE_MIN_IN_MILLIS = 60 * 1000 * 60;
    private final String secretKey;
    private final long expirationTime;

    public JwtService(String secretKey, Integer expirationInMinutes) {
        this.secretKey = secretKey;
        this.expirationTime = expirationInMinutes * ONE_MIN_IN_MILLIS;
    }

    public String generateToken(String user, Map<String, Object> claims) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateTokenExpiration(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expirationDate = claims.getExpiration();
            return expirationDate.after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public String getUserEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
