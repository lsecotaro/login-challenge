package com.lsecotaro.login_challenge.auth.service.parameter;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SignUpParameter {
    private final String name;
    private final String email;
    private final String password;
    private final List<Phone> phones;
}
