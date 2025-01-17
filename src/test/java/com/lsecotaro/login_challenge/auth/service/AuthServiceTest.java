package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.auth.model.User;
import com.lsecotaro.login_challenge.auth.repository.UserPhoneRepository;
import com.lsecotaro.login_challenge.auth.repository.UserRepository;
import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameter;
import com.lsecotaro.login_challenge.auth.service.validator.SigUpValidator;
import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import com.lsecotaro.login_challenge.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

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

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPhoneRepository userPhoneRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignUpSuccess() {
        SignUpParameter parameters = SignUpParameter.builder()
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
        when(userRepository.save(any())).thenReturn(User.builder().id(UUID.randomUUID().toString()).build());

        authService.signUp(parameters);

        verify(sigUpValidator).validate(parameters);
        verify(passwordEncoder).encode(parameters.getPassword());
        verify(jwtGenerator).generateToken(eq(parameters.getEmail()), anyMap());
        verify(userRepository).save(any());
        verify(userPhoneRepository, never()).save(any());
    }

    @Test
    public void testSignUpWithInvalidPassword() {
        // Arrange
        SignUpParameter parameters = SignUpParameter.builder()
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
        SignUpParameter parameters = SignUpParameter.builder()
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
        when(userRepository.save(any())).thenReturn(User.builder().id(UUID.randomUUID().toString()).build());

        authService.signUp(parameters);

        verify(sigUpValidator).validate(parameters);
        verify(passwordEncoder).encode(parameters.getPassword());
        verify(jwtGenerator).generateToken(eq(parameters.getEmail()), anyMap());
        verify(userRepository).save(any());
        verify(userPhoneRepository, never()).save(any());
    }

    @Test
    public void testSignUpWithPhones() {
        SignUpParameter parameters = SignUpParameter.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("ValidPass123")
                .phones(Collections.singletonList(Phone.builder()
                        .number(123456789L)
                        .cityCode(911)
                        .countryCode("+55")
                        .build()))
                .build();

        String encodedPassword = "encodedPassword123";
        String token = "mockedJwtToken";

        doNothing().when(sigUpValidator).validate(parameters);
        when(passwordEncoder.encode(parameters.getPassword())).thenReturn(encodedPassword);
        when(jwtGenerator.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);
        when(userRepository.save(any())).thenReturn(User.builder().id(UUID.randomUUID().toString()).build());

        authService.signUp(parameters);

        verify(sigUpValidator).validate(parameters);
        verify(passwordEncoder).encode(parameters.getPassword());
        verify(jwtGenerator).generateToken(eq(parameters.getEmail()), anyMap());
        verify(userRepository).save(any());
        verify(userPhoneRepository).saveAll(any());
    }

    @Test
    public void testSignUpUserAlreadyExists() {
        SignUpParameter parameters = SignUpParameter.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("invalid")
                .phones(Collections.emptyList())
                .build();

        String encodedPassword = "encodedPassword123";
        String token = "mockedJwtToken";

        doNothing().when(sigUpValidator).validate(parameters);
        when(passwordEncoder.encode(parameters.getPassword())).thenReturn(encodedPassword);
        when(jwtGenerator.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(
                User.builder().id(UUID.randomUUID().toString()).build()));


        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.signUp(parameters);
        });

        verify(sigUpValidator).validate(parameters);
        verifyNoInteractions(passwordEncoder, jwtGenerator);
    }

}
