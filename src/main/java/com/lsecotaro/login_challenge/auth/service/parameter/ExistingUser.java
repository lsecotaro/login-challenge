package com.lsecotaro.login_challenge.auth.service.parameter;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class ExistingUser {
    private final String id;
    private final Date created;
    private final Date lastLogin;
    private final String name;
    private final String token;
    private final boolean isActive;
    private final String email;
    private final String password;
    private final List<Phone> phones;
}
