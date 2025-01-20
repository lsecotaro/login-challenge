package com.lsecotaro.login_challenge.auth.repository;

import com.lsecotaro.login_challenge.auth.model.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhoneRepository extends JpaRepository<UserPhone, String> {
}
