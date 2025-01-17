package com.lsecotaro.login_challenge.auth.repository;

import com.lsecotaro.login_challenge.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
