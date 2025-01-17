package com.lsecotaro.login_challenge.auth.service.validator;

import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameter;
import com.lsecotaro.login_challenge.exception.InvalidPasswordException;
import com.lsecotaro.login_challenge.exception.InvalidPhoneException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
public class SigUpValidatorTest {
    private SigUpValidator sigUpValidator;

    @BeforeEach
    public void setUp() {
        sigUpValidator = new SigUpValidator();
    }

    @Test
    public void testValidPassword() {
        String validPassword = "a2asfGfdfdf4"; // Matches the password pattern
        assertTrue(sigUpValidator.isValidPassword(validPassword));
    }

    @Test
    public void testInvalidPasswordTooShort() {
        String shortPassword = "a2aGf"; // Less than 8 characters
        assertFalse(sigUpValidator.isValidPassword(shortPassword));
    }

    @Test
    public void testInvalidPasswordTooLong() {
        String longPassword = "a2asfGfdfdfdfdf"; // More than 12 characters
        assertFalse(sigUpValidator.isValidPassword(longPassword));
    }

    @Test
    public void testInvalidPasswordNoUppercase() {
        String noUppercasePassword = "a2asfghdf4"; // No uppercase letter
        assertFalse(sigUpValidator.isValidPassword(noUppercasePassword));
    }

    @Test
    public void testInvalidPasswordNoTwoNumbers() {
        String noTwoNumbersPassword = "aBsdfgH4"; // Only one number
        assertFalse(sigUpValidator.isValidPassword(noTwoNumbersPassword));
    }

    @Test
    public void testInvalidPasswordNoNumbers() {
        String noNumbersPassword = "aBsdfgH"; // No numbers
        assertFalse(sigUpValidator.isValidPassword(noNumbersPassword));
    }

    @Test()
    public void testValidatePasswordInvalid() {
        SignUpParameter invalidParameters = SignUpParameter.builder()
                .password("invalidPassword")
                .build();
        assertThrows(InvalidPasswordException.class, () -> {
            sigUpValidator.validate(invalidParameters);
        });
    }

    @Test
    public void testValidatePasswordValid() {
        SignUpParameter validParameters = SignUpParameter.builder()
                .password("a2asfGfdfdf4")
                .build();
        sigUpValidator.validate(validParameters); // Should not throw any exception
    }

    @Test()
    public void testValidatePhoneNumberInvalid() {
        SignUpParameter invalidParameters = SignUpParameter.builder()
                .password("a2asfGfdfdf4")
                .phones(List.of(Phone.builder()
                        .number(123L)
                        .build()))
                .build();
        assertThrows(InvalidPhoneException.class, () -> {
            sigUpValidator.validate(invalidParameters);
        });
    }

    @Test()
    public void testValidateNullPhoneNumberInvalid() {
        SignUpParameter invalidParameters = SignUpParameter.builder()
                .password("a2asfGfdfdf4")
                .phones(List.of(Phone.builder()
                        .number(null)
                        .build()))
                .build();
        assertThrows(InvalidPhoneException.class, () -> {
            sigUpValidator.validate(invalidParameters);
        });
    }
    @Test()
    public void testValidatePhoneCityInvalid() {
        SignUpParameter invalidParameters = SignUpParameter.builder()
                .password("a2asfGfdfdf4")
                .phones(List.of(Phone.builder()
                        .number(123456L)
                        .cityCode(12)
                        .build()))
                .build();
        assertThrows(InvalidPhoneException.class, () -> {
            sigUpValidator.validate(invalidParameters);
        });
    }

    @Test()
    public void testValidateNullPhoneCityInvalid() {
        SignUpParameter invalidParameters = SignUpParameter.builder()
                .password("a2asfGfdfdf4")
                .phones(List.of(Phone.builder()
                        .number(123456L)
                        .cityCode(null)
                        .build()))
                .build();
        assertThrows(InvalidPhoneException.class, () -> {
            sigUpValidator.validate(invalidParameters);
        });
    }

    @Test()
    public void testPhoneValid() {
        SignUpParameter invalidParameters = SignUpParameter.builder()
                .password("a2asfGfdfdf4")
                .phones(List.of(Phone.builder()
                        .number(123456L)
                        .cityCode(123)
                        .build()))
                .build();
        sigUpValidator.validate(invalidParameters);
    }

}