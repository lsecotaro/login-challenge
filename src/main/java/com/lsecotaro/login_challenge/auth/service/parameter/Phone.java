package com.lsecotaro.login_challenge.auth.service.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    private Long number;
    @JsonProperty("citycode")
    private Integer cityCode;
    @JsonProperty("countycode")
    private String countryCode;
}
