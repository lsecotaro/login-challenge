package com.lsecotaro.login_challenge.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorCode {
    REQUIRED_PARAM_MISSING("Required parameter missing", 1001),
    INVALID_PASSWORD("Invalid Password", 1002),
    USER_ALREADY_EXISTS("User Already Exists", 2001);

    private final String description;
    private final int code;
}
