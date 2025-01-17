package com.lsecotaro.login_challenge.auth.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneDto {
    @Positive(message = "number must be positive")
    private Long number;

    @JsonProperty("citycode")
    @Positive(message = "city code must be positive")
    private Integer cityCode;

    @JsonProperty("countrycode")
    @Size(min = 3, max = 4, message = "country code must be 3 or 4 characters long")
    private String countryCode;
}
