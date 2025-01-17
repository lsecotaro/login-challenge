package com.lsecotaro.login_challenge.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPhone {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    private Long phoneNumber;
    private Integer cityCode;
    private String countryCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
