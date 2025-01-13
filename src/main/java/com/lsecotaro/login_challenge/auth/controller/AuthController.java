package com.lsecotaro.login_challenge.auth.controller;

import com.lsecotaro.login_challenge.auth.service.AuthService;
import com.lsecotaro.login_challenge.auth.controller.response.PingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = org.springframework.http.HttpStatus.OK)
    public PingResponseDto ping() {
        log.info("ping");
        return PingResponseDto.builder()
                .pong(authService.ping())
                .build();
    }
}
