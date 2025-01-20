package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class JwtServiceTest {
    public static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";
    private static final String TEST_TOKEN = "token";

    private Claims claims;

    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        claims = Mockito.mock(Claims.class);
        jwtService = new JwtService(SECRET_KEY, 10);
    }


    @Test
    public void testGenerateToken() {
        String username = "testUser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");

        String token = jwtService.generateToken(username, claims);

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

        String token = jwtService.generateToken(username, claims);

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

    @Test
    public void testValidateTokenExpiration_validToken() {
        Date validExpirationDate = new Date(System.currentTimeMillis() + 100000);
        when(claims.getExpiration()).thenReturn(validExpirationDate);

        jwtService = Mockito.spy(jwtService);
        doReturn(claims).when(jwtService).extractClaims(TEST_TOKEN);

        boolean result = jwtService.validateTokenExpiration(TEST_TOKEN);
        assertTrue(result);
    }

    @Test
    public void testValidateTokenExpiration_expiredToken() {
        Date expiredExpirationDate = new Date(System.currentTimeMillis() - 100000);
        when(claims.getExpiration()).thenReturn(expiredExpirationDate);

        jwtService = Mockito.spy(jwtService);
        doReturn(claims).when(jwtService).extractClaims(TEST_TOKEN);

        boolean result = jwtService.validateTokenExpiration(TEST_TOKEN);
        assertFalse(result);
    }

    @Test
    public void testValidateTokenExpiration_invalidToken() {
        jwtService = Mockito.spy(jwtService);
        doThrow(new ExpiredJwtException(null, null, "Expired")).when(jwtService).extractClaims(TEST_TOKEN);

        boolean result = jwtService.validateTokenExpiration(TEST_TOKEN);
        assertFalse(result);
    }

    @Test
    public void testGetUserEmail() {
        String expectedEmail = "test@example.com";
        when(claims.getSubject()).thenReturn(expectedEmail);

        String result = jwtService.getUserEmail(jwtService.generateToken(expectedEmail, new HashMap<>()));
        assertEquals(expectedEmail, result);
    }
}
