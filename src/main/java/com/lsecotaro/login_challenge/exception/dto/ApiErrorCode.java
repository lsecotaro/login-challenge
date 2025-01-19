package com.lsecotaro.login_challenge.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorCode {
    REQUIRED_PARAM_MISSING("Required parameter missing", 1001),
    INVALID_PASSWORD("Invalid Password", 1002),
    INVALID_PHONE("Invalid Phone", 1003),
    USER_ALREADY_EXISTS("User Already Exists", 2001),
    USER_NOT_FOUND("User Not Found", 2002),
    USER_NOT_ACTIVE("User Not Active", 2003);

    private final String description;
    private final int code;
}
