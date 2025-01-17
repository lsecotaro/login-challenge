package com.lsecotaro.login_challenge.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsecotaro.login_challenge.auth.controller.request.PhoneDto;
import com.lsecotaro.login_challenge.auth.controller.request.SignUpRequestDto;
import com.lsecotaro.login_challenge.config.TestSecurityConfig;
import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import com.lsecotaro.login_challenge.exception.InvalidPhoneException;
import com.lsecotaro.login_challenge.exception.UserAlreadyExistsException;
import com.lsecotaro.login_challenge.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = TestSecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthMapper authMapper;

    private static final String SIGN_UP_URL = "/auth/v1/sign-up";


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
}
