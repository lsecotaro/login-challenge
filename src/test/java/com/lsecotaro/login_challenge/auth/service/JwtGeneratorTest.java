package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class JwtGeneratorTest {
    public static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";
    private JwtGenerator jwtGenerator;

    @BeforeEach
    public void setUp() {
        jwtGenerator = new JwtGenerator();
    }

    @Test
    public void testGenerateToken() {
        String username = "testUser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");

        String token = jwtGenerator.generateToken(username, claims);

        assertNotNull(token);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Claims decodedClaims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, decodedClaims.getSubject());
        assertEquals("admin", decodedClaims.get("role"));
        assertNotNull(decodedClaims.getIssuedAt());
        assertNotNull(decodedClaims.getExpiration());

        long expirationTimeInMillis = decodedClaims.getExpiration().getTime() - decodedClaims.getIssuedAt().getTime();
        long expectedExpirationTimeInMillis = 10 * 60 * 60 * 1000; // 10 hours in milliseconds
        assertTrue(Math.abs(expirationTimeInMillis - expectedExpirationTimeInMillis) < 1000); // Allow a small margin for execution time
    }


    @Test
    public void testGenerateTokenWithEmptyClaims() {
        String username = "testUser";
        Map<String, Object> claims = new HashMap<>();

        String token = jwtGenerator.generateToken(username, claims);

        assertNotNull(token);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Claims decodedClaims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, decodedClaims.getSubject());
        assertNotNull(decodedClaims.getIssuedAt());
        assertNotNull(decodedClaims.getExpiration());
    }
}