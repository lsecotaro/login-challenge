package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.auth.model.User;
import com.lsecotaro.login_challenge.auth.model.UserPhone;
import com.lsecotaro.login_challenge.auth.repository.UserPhoneRepository;
import com.lsecotaro.login_challenge.auth.repository.UserRepository;
import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameter;
import com.lsecotaro.login_challenge.auth.service.validator.SigUpValidator;
import com.lsecotaro.login_challenge.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final SigUpValidator sigUpParametersValidator;
    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserPhoneRepository userPhoneRepository;

    public void signUp(SignUpParameter parameter) {
        log.info("Processing signUp");

        validate(parameter);
        String token = generateToken(parameter);
        User createdUser = saveUser(parameter, token);
        savePhones(createdUser, parameter.getPhones());

        log.info("User created: {}", createdUser.getId());
    }

    private void savePhones(User createdUser, List<Phone> phones) {
        if (Objects.isNull(phones)) {
            return;
        }

        List<UserPhone> userPhones = new ArrayList<>();
        for(Phone phone : phones) {
            UserPhone userPhone = UserPhone.builder()
                    .user(createdUser)
                    .phoneNumber(phone.getNumber())
                    .cityCode(phone.getCityCode())
                    .countryCode(phone.getCountryCode())
                    .build();
            userPhones.add(userPhone);
        }
        userPhoneRepository.saveAll(userPhones);
    }

    private User saveUser(SignUpParameter parameter, String token) {
        User newUser = User.builder()
            .name(parameter.getName())
            .password(passwordEncoder.encode(parameter.getPassword()))
            .email(parameter.getEmail())
            .token(token)
            .createdAt(new Date())
            .active(true)
            .build();

        return userRepository.save(newUser);
    }

    private String generateToken(SignUpParameter parameter) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "operator");
        String token = jwtGenerator.generateToken(parameter.getEmail(), claims);
        return token;
    }

    private void validate(SignUpParameter parameter) {
        sigUpParametersValidator.validate(parameter);

        Optional<User> existingUser = userRepository.findByEmail(parameter.getEmail());
        if(existingUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists.", parameter.getEmail()));
        }
    }
}
