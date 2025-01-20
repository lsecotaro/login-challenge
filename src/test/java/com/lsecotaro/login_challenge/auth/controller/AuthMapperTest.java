package com.lsecotaro.login_challenge.auth.controller;

import com.lsecotaro.login_challenge.auth.controller.request.PhoneDto;
import com.lsecotaro.login_challenge.auth.controller.request.SignUpRequestDto;
import com.lsecotaro.login_challenge.auth.controller.response.LoginResponseDto;
import com.lsecotaro.login_challenge.auth.service.parameter.ExistingUser;
import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameter;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
public class AuthMapperTest {
    private final AuthMapper authMapper = new AuthMapper();

    @Test
    void testToParameter() {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Password123")
                .phones(Arrays.asList(
                        PhoneDto.builder()
                                .number(123456789L)
                                .cityCode(1)
                                .countryCode("+1")
                                .build(),
                        PhoneDto.builder()
                                .number(987654321L)
                                .cityCode(2)
                                .countryCode("+2")
                                .build()
                ))
                .build();

        SignUpParameter parameters = authMapper.toParameter(requestDto);

        assertNotNull(parameters);
        assertEquals("John Doe", parameters.getName());
        assertEquals("john.doe@example.com", parameters.getEmail());
        assertEquals("Password123", parameters.getPassword());
        assertNotNull(parameters.getPhones());
        assertEquals(2, parameters.getPhones().size());
        assertEquals(123456789L, (long) parameters.getPhones().get(0).getNumber());
        assertEquals(1, (int) parameters.getPhones().get(0).getCityCode());
        assertEquals("+1", parameters.getPhones().get(0).getCountryCode());
    }

    @Test
    void testToDto() {
        ExistingUser existingUser = ExistingUser.builder()
                .id("123")
                .isActive(true)
                .created(new Date())
                .name("John Doe")
                .password("Password123")
                .email("john.doe@example.com")
                .token("token123")
                .phones(Arrays.asList(
                        Phone.builder()
                                .number(123456789L)
                                .cityCode(1)
                                .countryCode("+1")
                                .build(),
                        Phone.builder()
                                .number(987654321L)
                                .cityCode(2)
                                .countryCode("+2")
                                .build()
                ))
                .build();

        LoginResponseDto responseDto = authMapper.toDto(existingUser);

        assertNotNull(responseDto);
        assertEquals("123", responseDto.getId());
        assertTrue(responseDto.isActive());
        assertEquals("John Doe", responseDto.getName());
        assertEquals("Password123", responseDto.getPassword());
        assertEquals("john.doe@example.com", responseDto.getEmail());
        assertEquals("token123", responseDto.getToken());
        assertNotNull(responseDto.getPhones());
        assertEquals(2, responseDto.getPhones().size());
        assertEquals(123456789L, (long)responseDto.getPhones().get(0).getNumber());
        assertEquals(1, (int) responseDto.getPhones().get(0).getCityCode());
        assertEquals("+1", responseDto.getPhones().get(0).getCountryCode());
    }

    @Test
    void testToParameterWithNullPhones() {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .password("SecurePass123")
                .phones(null)
                .build();

        SignUpParameter parameters = authMapper.toParameter(requestDto);

        assertNotNull(parameters);
        assertEquals("Jane Doe", parameters.getName());
        assertEquals("jane.doe@example.com", parameters.getEmail());
        assertEquals("SecurePass123", parameters.getPassword());
        assertNull(parameters.getPhones());
    }

    @Test
    void testToDtoWithNullPhones() {
        ExistingUser existingUser = ExistingUser.builder()
                .id("456")
                .isActive(false)
                .created(new Date())
                .name("Jane Doe")
                .password("SecurePass123")
                .email("jane.doe@example.com")
                .token("token456")
                .phones(null)
                .build();

        LoginResponseDto responseDto = authMapper.toDto(existingUser);

        assertNotNull(responseDto);
        assertEquals("456", responseDto.getId());
        assertFalse(responseDto.isActive());
        assertEquals("Jane Doe", responseDto.getName());
        assertEquals("SecurePass123", responseDto.getPassword());
        assertEquals("jane.doe@example.com", responseDto.getEmail());
        assertEquals("token456", responseDto.getToken());
        assertNull(responseDto.getPhones());
    }
}