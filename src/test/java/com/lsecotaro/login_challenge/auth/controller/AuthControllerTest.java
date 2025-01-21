package com.lsecotaro.login_challenge.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsecotaro.login_challenge.auth.controller.request.PhoneDto;
import com.lsecotaro.login_challenge.auth.controller.request.SignUpRequestDto;
import com.lsecotaro.login_challenge.auth.controller.response.LoginResponseDto;
import com.lsecotaro.login_challenge.auth.service.AuthService;
import com.lsecotaro.login_challenge.auth.service.parameter.ExistingUser;
import com.lsecotaro.login_challenge.config.TestSecurityConfig;
import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import com.lsecotaro.login_challenge.exception.InvalidPhoneException;
import com.lsecotaro.login_challenge.exception.UserAlreadyExistsException;
import com.lsecotaro.login_challenge.exception.UserNotActiveException;
import com.lsecotaro.login_challenge.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {
    private static final String SIGN_UP_URL = "/auth/v1/public/sign-up";
    public static final String LOGIN_URL = "/auth/v1/login";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthMapper authMapper;


    @Test
    public void signUpValidRequestReturnsCreated() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("valid.email@example.com")
                .password("Valid123")
                .name("John Doe")
                .build();

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void signUpInvalidEmailReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("invalid-email");
        request.setPassword("Valid123");
        request.setName("John Doe");

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("email must be valid"));
    }

    @Test
    public void signUpNullPasswordReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("valid.email@example.com");
        request.setName("John Doe");

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("password must not be null"));
    }

    @Test
    public void signUpInvalidPasswordReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("valid.email@example.com");
        request.setName("John Doe");
        request.setPassword("lalala");

        doThrow(new InvalidPasswordException()).when(authService).signUp(any());

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("Invalid Password"));
    }

    @Test
    public void signUpMissingRequiredFieldsReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setName("John Doe");

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").exists())
                .andExpect(jsonPath("$.error[1].detail").exists());
    }

    @Test
    public void signUpInvalidNameReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("valid.email@example.com");
        request.setName("Jo");
        request.setPassword("Valid123");

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("name must be between 3 and 100 characters"));
    }

    @Test
    public void signUpInvalidPhoneNumberReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("valid.email@example.com");
        request.setPassword("a2asfGfdfdf4");
        request.setPhones(List.of(PhoneDto.builder()
                        .number(0L)
                .build()));

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("number must be positive"));
    }

    @Test
    public void signUpInvalidPhoneCityReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("valid.email@example.com");
        request.setPassword("a2asfGfdfdf4");
        request.setPhones(List.of(PhoneDto.builder()
                .number(12313L)
                .cityCode(-1)
                .build()));

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("city code must be positive"));
    }

    @Test
    public void signUpInvalidPhoneCountryReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("valid.email@example.com");
        request.setPassword("a2asfGfdfdf4");
        request.setPhones(List.of(PhoneDto.builder()
                .number(12313L)
                .cityCode(1)
                .countryCode("12")
                .build()));

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("country code must be 3 or 4 characters long"));
    }

    @Test
    public void signUpUserAlreadyExistsReturnsConflict() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("existing.email@example.com");
        request.setPassword("Valid123");
        request.setName("John Doe");

        doThrow(new UserAlreadyExistsException("User Already Exists")).when(authService).signUp(any());

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error[0].detail").value("User Already Exists"));
    }

    @Test
    public void signUpInvalidPhoneReturnsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("existing.email@example.com");
        request.setPassword("Valid123");
        request.setName("John Doe");

        doThrow(new InvalidPhoneException()).when(authService).signUp(any());

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").exists());
    }

    @Test
    void testLoginSuccess() throws Exception {
        String email = "user@example.com";

        ExistingUser existingUser = ExistingUser.builder().email(email).build();
        LoginResponseDto responseDto = LoginResponseDto.builder().email(email).build();

        when(authService.findActiveUser(anyString(), anyString())).thenReturn(existingUser);
        when(authMapper.toDto(any())).thenReturn(responseDto);

        mockMvc.perform(post(LOGIN_URL)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(email));

        verify(authService, times(1)).findActiveUser(anyString(), anyString());
        verify(authMapper, times(1)).toDto(any());
    }

    @Test
    void testLoginNoAuthentication() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get(LOGIN_URL)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testLoginUserNoActiveReturnsUnauthorized() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("existing.email@example.com");
        request.setPassword("Valid123");
        request.setName("John Doe");

        doThrow(new UserNotActiveException("User Not Active")).when(authService).findActiveUser(anyString(), anyString());

        mockMvc.perform(post(LOGIN_URL)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error[0].detail").exists());
    }

    @Test
    public void testLoginUserNoFoundReturnsUnauthorized() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("notFound.email@example.com");
        request.setPassword("Valid123");
        request.setName("John Doe");

        doThrow(new UserNotFoundException("User Not Found")).when(authService).findActiveUser(anyString(), anyString());

        mockMvc.perform(post(LOGIN_URL)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error[0].detail").exists());
    }
}
