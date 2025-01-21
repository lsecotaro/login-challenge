package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.auth.model.User;
import com.lsecotaro.login_challenge.auth.model.UserPhone;
import com.lsecotaro.login_challenge.auth.repository.UserPhoneRepository;
import com.lsecotaro.login_challenge.auth.repository.UserRepository;
import com.lsecotaro.login_challenge.auth.service.parameter.ExistingUser;
import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameter;
import com.lsecotaro.login_challenge.auth.service.validator.SigUpValidator;
import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import com.lsecotaro.login_challenge.exception.UserAlreadyExistsException;
import com.lsecotaro.login_challenge.exception.UserNotActiveException;
import com.lsecotaro.login_challenge.exception.UserNotFoundException;
import com.lsecotaro.login_challenge.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class AuthServiceTest {
    @Mock
    private SigUpValidator sigUpValidator;

    @Mock
    private JwtService jwtservice;

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
        when(jwtservice.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);
        when(userRepository.save(any())).thenReturn(User.builder().id(UUID.randomUUID().toString()).build());

        authService.signUp(parameters);

        verify(sigUpValidator).validate(parameters);
        verify(passwordEncoder).encode(parameters.getPassword());
        verify(jwtservice).generateToken(eq(parameters.getEmail()), anyMap());
        verify(userRepository).save(any());
        verify(userPhoneRepository, never()).save(any());
    }

    @Test
    public void testSignUpWithInvalidPassword() {
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
        verifyNoInteractions(passwordEncoder, jwtservice);
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
        when(jwtservice.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);
        when(userRepository.save(any())).thenReturn(User.builder().id(UUID.randomUUID().toString()).build());

        authService.signUp(parameters);

        verify(sigUpValidator).validate(parameters);
        verify(passwordEncoder).encode(parameters.getPassword());
        verify(jwtservice).generateToken(eq(parameters.getEmail()), anyMap());
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
        when(jwtservice.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);
        when(userRepository.save(any())).thenReturn(User.builder().id(UUID.randomUUID().toString()).build());

        authService.signUp(parameters);

        verify(sigUpValidator).validate(parameters);
        verify(passwordEncoder).encode(parameters.getPassword());
        verify(jwtservice).generateToken(eq(parameters.getEmail()), anyMap());
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
        when(jwtservice.generateToken(eq(parameters.getEmail()), anyMap())).thenReturn(token);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(
                User.builder().id(UUID.randomUUID().toString()).build()));


        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.signUp(parameters);
        });

        verify(sigUpValidator).validate(parameters);
        verifyNoInteractions(passwordEncoder, jwtservice);
    }

    @Test
    void testFindActiveUserUserIsActive() {
        String email = "active.user@example.com";
        String token = "valid_token";
        User mockUser =User.builder()
                .id("1")
                .createdAt(new Date())
                .lastLogin(new Date())
                .name("Active User")
                .token(token)
                .active(true)
                .email(email)
                .password("hashed_password")
                .phones(List.of(UserPhone.builder()
                        .phoneNumber(12345678L)
                        .cityCode(911)
                        .countryCode("+55")
                        .build()))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        ExistingUser existingUser = authService.findActiveUser(email, token);

        assertNotNull(existingUser);
        assertEquals(mockUser.getId(), existingUser.getId());
        assertEquals(mockUser.getName(), existingUser.getName());
        assertEquals(mockUser.getEmail(), existingUser.getEmail());
        assertTrue(existingUser.isActive());
        assertEquals(mockUser.getPhones().size(), existingUser.getPhones().size());
        assertEquals(mockUser.getPhones().get(0).getPhoneNumber(), existingUser.getPhones().get(0).getNumber());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testFindInActiveUserUserIsInactive() {
        String email = "inactive.user@example.com";
        String token = "valid_token";
        User mockUser = User.builder()
                        .active(false)
                        .token(token)
                        .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserNotActiveException exception = assertThrows(UserNotActiveException.class, () -> authService.findActiveUser(email, token));
        assertEquals(String.format("User with email %s not active", email), exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindActiveUserWithInactiveUserIsInactive() {
        String email = "inactive.user@example.com";
        String token = "inactive_token";
        User mockUser = User.builder()
                .active(true)
                .token("token")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserNotActiveException exception = assertThrows(UserNotActiveException.class, () -> authService.findActiveUser(email, token));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindActiveUserUserNotFound() {
        String email = "nonexistent.user@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.findActiveUser(email, "token"));
        assertEquals(String.format("User with email %s not found", email), exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
