package com.lsecotaro.login_challenge.auth.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "email must not be null")
    @Email(message = "email must be valid")
    private String email;

    @NotNull(message = "password must not be null")
    private String password;

    @Valid
    private List<PhoneDto> phones;
}
