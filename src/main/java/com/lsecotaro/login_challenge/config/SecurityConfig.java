package com.lsecotaro.login_challenge.config;

import com.lsecotaro.login_challenge.auth.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationFilter authenticationFilter;
    private static final String[] AUTH_WHITELIST = {
            "/api/swagger-resources/**",
            "/api/swagger-ui.html",
            "/api/swagger-ui/**",
            "/api/v2/api-docs",
            "/api/webjars/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().disable()
            .securityContext().disable()
            .headers().frameOptions().disable()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(AUTH_WHITELIST).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
