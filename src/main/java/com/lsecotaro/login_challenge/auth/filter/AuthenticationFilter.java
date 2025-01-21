package com.lsecotaro.login_challenge.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.lsecotaro.login_challenge.exception.dto.ApiErrorCode;
import com.lsecotaro.login_challenge.exception.dto.ErrorDetailDto;
import com.lsecotaro.login_challenge.exception.dto.ErrorResponseDto;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String ANONYMOUS = "anonymous";

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
            buildErrorResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void buildErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .errors(List.of(ErrorDetailDto.builder()
                        .timestamp(new Date())
                        .code(ApiErrorCode.TOKEN_EXPIRED_OR_INVALID.getCode())
                        .detail("Token is expired or invalid")
                        .build()))
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());
        String jsonResponse = mapper.writeValueAsString(errorResponseDto);

        response.setContentType("application/json");

        response.getWriter().write(jsonResponse);
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
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
