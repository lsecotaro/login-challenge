package com.lsecotaro.login_challenge.auth.service;

import com.lsecotaro.login_challenge.auth.model.User;
import com.lsecotaro.login_challenge.auth.model.UserPhone;
import com.lsecotaro.login_challenge.auth.repository.UserPhoneRepository;
import com.lsecotaro.login_challenge.auth.repository.UserRepository;
import com.lsecotaro.login_challenge.auth.service.parameter.ExistingUser;
import com.lsecotaro.login_challenge.auth.service.parameter.Phone;
import com.lsecotaro.login_challenge.auth.service.parameter.SignUpParameter;
import com.lsecotaro.login_challenge.auth.service.validator.SigUpValidator;
import com.lsecotaro.login_challenge.exception.UserAlreadyExistsException;
import com.lsecotaro.login_challenge.exception.UserNotActiveException;
import com.lsecotaro.login_challenge.exception.UserNotFoundException;
import com.lsecotaro.login_challenge.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final SigUpValidator sigUpParametersValidator;
    private final JwtService jwtservice;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserPhoneRepository userPhoneRepository;

    public void signUp(SignUpParameter parameter) {
        log.info("Processing signUp");

        validateSignUp(parameter);
        String token = generateToken(parameter.getEmail());
        User createdUser = saveUser(parameter, token);
        savePhones(createdUser, parameter.getPhones());

        log.info("User created: {}", createdUser.getId());
    }

    public ExistingUser findActiveUser(String email, String token) {
        User user = findUser(email);

        validateLogin(email, token, user);

        updateUserToken(email, user);

        return ExistingUser.builder()
                .id(user.getId())
                .created(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .name(user.getName())
                .token(user.getToken())
                .isActive(user.isActive())
                .email(user.getEmail())
                .password(user.getPassword())
                .phones(user.getPhones().stream()
                        .map(phone -> Phone.builder()
                            .number(phone.getPhoneNumber())
                            .cityCode(phone.getCityCode())
                            .countryCode(phone.getCountryCode())
                            .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private void updateUserToken(String email, User user) {
        user.setLastLogin(new Date());
        user.setToken(generateToken(email));
        userRepository.save(user);
    }

    private void validateLogin(String email, String token, User user) {
        if (!user.isActive()) {
            throw new UserNotActiveException(String.format("User with email %s not active", email));
        }
        if (!token.equals(user.getToken())) {
            throw new UserNotActiveException(String.format("Inactive token for user with email %s", email));
        }
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

    private String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "operator");
        return jwtservice.generateToken(email, claims);
    }

    private void validateSignUp(SignUpParameter parameter) {
        sigUpParametersValidator.validate(parameter);

        Optional<User> existingUser = userRepository.findByEmail(parameter.getEmail());
        if(existingUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists.", parameter.getEmail()));
        }
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
    }
}
