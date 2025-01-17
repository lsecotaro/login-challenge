package com.lsecotaro.login_challenge.auth.controller.response;

import com.lsecotaro.login_challenge.auth.controller.request.PhoneDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponseDto {
    private String id;
    private Date created;
    private Date lastLogin;
    private String name;
    private String token;
    private boolean isActive;
    private String email;
    private String password;
    private List<PhoneDto> phones;
}
