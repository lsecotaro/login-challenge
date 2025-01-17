package com.lsecotaro.login_challenge.auth.controller;

import com.lsecotaro.login_challenge.auth.controller.request.SignUpRequestDto;
import com.lsecotaro.login_challenge.auth.controller.response.SignUpResponseDto;
import com.lsecotaro.login_challenge.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("auth/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AuthMapper authMapper;

    @PostMapping(value = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public SignUpResponseDto signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        log.info("Requesting signUp");

        authService.signUp(authMapper.toParameter(signUpRequestDto));

        return SignUpResponseDto.builder()
                .message("User created successfully.")
                .build();
    }
}
