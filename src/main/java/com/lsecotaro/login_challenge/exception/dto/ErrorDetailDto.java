package com.lsecotaro.login_challenge.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetailDto {
    private Date timestamp;
    private int code;
    private String detail;
}
