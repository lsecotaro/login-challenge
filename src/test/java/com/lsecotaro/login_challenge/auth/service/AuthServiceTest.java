package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.auth.service.parameter.CreatedUser;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameters;
import com.lsecotaro.login_challenge.auth.service.validator.SigUpValidator;
import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class AuthServiceTest {
    @Mock
    private SigUpValidator sigUpValidator;

    @Mock
    private JwtGenerator jwtGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignUpSuccess() {
        SignUpParameters parameters = SignUpParameters.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("ValidPass123")
                .phones(Collections.emptyList())
                .build();

        String encodedPassword = "encodedPassword123";
        String token = "mockedJwtToken";

        doNothing().when(sigUpValidator).validate(parameters);
        when(passwordEncoder.encode(parameters.getPassword())).thenReturn(encodedPassword);
        when(jwtGenerator.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);

        CreatedUser createdUser = authService.signUp(parameters);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(parameters.getName(), createdUser.getName());
        assertEquals(encodedPassword, createdUser.getPassword());
        assertEquals(parameters.getEmail(), createdUser.getEmail());
        assertEquals(token, createdUser.getToken());
        assertTrue(createdUser.isActive());
        assertNotNull(createdUser.getCreated());

        verify(sigUpValidator).validate(parameters);
        verify(passwordEncoder).encode(parameters.getPassword());
        verify(jwtGenerator).generateToken(eq(parameters.getEmail()), anyMap());
    }

    @Test
    public void testSignUpWithInvalidPassword() {
        // Arrange
        SignUpParameters parameters = SignUpParameters.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("invalid")
                .phones(Collections.emptyList())
                .build();

        doThrow(new InvalidPasswordException()).when(sigUpValidator).validate(parameters);

        assertThrows(InvalidPasswordException.class, () -> {
            authService.signUp(parameters);
        });

        verify(sigUpValidator).validate(parameters);
        verifyNoInteractions(passwordEncoder, jwtGenerator);
    }

    @Test
    public void testSignUpWithEmptyPhones() {
        SignUpParameters parameters = SignUpParameters.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("ValidPass123")
                .phones(null)
                .build();

        String encodedPassword = "encodedPassword123";
        String token = "mockedJwtToken";

        doNothing().when(sigUpValidator).validate(parameters);
        when(passwordEncoder.encode(parameters.getPassword())).thenReturn(encodedPassword);
        when(jwtGenerator.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);

        CreatedUser createdUser = authService.signUp(parameters);

        assertNotNull(createdUser);
        assertNull(createdUser.getPhones());
    }
}