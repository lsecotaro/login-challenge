package com.lsecotaro.login_challenge.auth.service.validator;

import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameters;
import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import com.lsecotaro.login_challenge.exception.InvalidPhoneException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SigUpValidator {
    private static final String PASSWORD_REGEX = "^(?=([^A-Z]*[A-Z]){1}[^A-Z]*$)(?=(\\D*\\d){2}\\D*$)[a-zA-Z\\d]{8,12}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final int PHONE_NUMBER_LENGTH = 6;
    private static final int PHONE_CITY_LENGTH = 3;


    public boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public void validate(SignUpParameters parameter) {
        if (!isValidPassword(parameter.getPassword())) {
            throw new InvalidPasswordException();
        }
        if (Objects.nonNull(parameter.getPhones()) && !isValidPhone(parameter.getPhones())) {
            throw new InvalidPhoneException();
        }
    }

    private boolean isValidPhone(List<Phone> phones) {
        for (Phone phone : phones) {
            if (String.valueOf(phone.getNumber()).length() != PHONE_NUMBER_LENGTH) {
                return false;
            }
            if (String.valueOf(phone.getCityCode()).length() != PHONE_CITY_LENGTH) {
                return false;
            }
        }
        return true;
    }
}
