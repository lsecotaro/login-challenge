package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.auth.service.parameter.CreatedUser;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameters;
import com.lsecotaro.login_challenge.auth.service.validator.SigUpValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final SigUpValidator sigUpValidator;
    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;

    public CreatedUser signUp(SignUpParameters parameter) {
        log.info("Processing signUp");

        sigUpValidator.validate(parameter);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "operator");
        String token = jwtGenerator.generateToken(parameter.getEmail(), claims);

        return CreatedUser.builder()
                .id(UUID.randomUUID().toString())
                .name(parameter.getName())
                .password(passwordEncoder.encode(parameter.getPassword()))
                .email(parameter.getEmail())
                .token(token)
                .phones(parameter.getPhones())
                .created(new Date())
                .isActive(true)
                .build();
    }
}
