package com.lsecotaro.login_challenge.auth.filter;

import com.lsecotaro.login_challenge.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ANONYMOUS = "anonymous";

    private final JwtService jwtService;
    private final Set<String> urlWithoutAuthentication;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (shouldNotFilter(request.getRequestURI())) {
            setAuthentication(ANONYMOUS);

            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (Objects.nonNull(token) && jwtService.validateTokenExpiration(token)) {
            String email = jwtService.getUserEmail(token);
            if (Objects.nonNull(email)) {
                setAuthentication(email);
            }
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token is expired or invalid");
            return;
        }

        filterChain.doFilter(request, response);
    }


    private boolean shouldNotFilter(String requestUrl) {
        return urlWithoutAuthentication.stream().anyMatch(requestUrl::contains);
    }

    private void setAuthentication(String anonymous) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(anonymous, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
