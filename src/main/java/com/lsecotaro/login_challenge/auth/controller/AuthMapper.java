package com.lsecotaro.login_challenge.auth.controller;

import com.lsecotaro.login_challenge.auth.controller.request.PhoneDto;
import com.lsecotaro.login_challenge.auth.controller.request.SignUpRequestDto;
import com.lsecotaro.login_challenge.auth.controller.response.LoginResponseDto;
import com.lsecotaro.login_challenge.auth.service.parameter.ExistingUser;
import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AuthMapper {
    public SignUpParameter toParameter(SignUpRequestDto signUpRequestDto) {
        return SignUpParameter.builder()
                .name(signUpRequestDto.getName())
                .email(normalizeString(signUpRequestDto.getEmail()))
                .password(signUpRequestDto.getPassword())
                .phones(Objects.isNull(signUpRequestDto.getPhones()) ? null : signUpRequestDto.getPhones().stream().map(this::toParameter).collect(Collectors.toList()))
                .build();
    }

    public LoginResponseDto toDto(ExistingUser user) {
        return LoginResponseDto.builder()
                .id(user.getId())
                .isActive(user.isActive())
                .created(user.getCreated())
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .token(user.getToken())
                .phones(Objects.isNull(user.getPhones()) ? null : user.getPhones().stream().map(this::toDto).collect(Collectors.toList()))
                .build();
    }

    private Phone toParameter(PhoneDto phoneDto) {
        return Phone.builder()
                .number(phoneDto.getNumber())
                .cityCode(phoneDto.getCityCode())
                .countryCode(phoneDto.getCountryCode())
                .build();
    }

    private PhoneDto toDto(Phone phone) {
        return PhoneDto.builder()
                .number(phone.getNumber())
                .cityCode(phone.getCityCode())
                .countryCode(phone.getCountryCode())
                .build();
    }

    private String normalizeString(String str) {
        return str.strip().toLowerCase();
    }
}
