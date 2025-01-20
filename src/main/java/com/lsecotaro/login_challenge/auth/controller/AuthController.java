package com.lsecotaro.login_challenge.auth.controller;

import com.lsecotaro.login_challenge.auth.controller.request.SignUpRequestDto;
import com.lsecotaro.login_challenge.auth.controller.response.LoginResponseDto;
import com.lsecotaro.login_challenge.auth.controller.response.SignUpResponseDto;
import com.lsecotaro.login_challenge.auth.service.AuthService;
import com.lsecotaro.login_challenge.auth.service.parameter.ExistingUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AuthMapper authMapper;

    @PostMapping(value = "/public/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public SignUpResponseDto signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        log.info("Requesting signUp");

        authService.signUp(authMapper.toParameter(signUpRequestDto));

        log.info("SignUp successfully");
        return SignUpResponseDto.builder()
                .message("User created successfully.")
                .build();
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = org.springframework.http.HttpStatus.OK)
    public LoginResponseDto login() {
        log.info("Requesting login");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ExistingUser existingUser = authService.findActiveUser(email);
        log.info("Login successful for user: {}", email);
        return authMapper.toDto(existingUser);
    }
}
