package com.lsecotaro.login_challenge.auth.filter;

import com.lsecotaro.login_challenge.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class AuthenticationFilterTest {
    private JwtService jwtService;

    private AuthenticationFilter authenticationFilter;

    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        jwtService = mock(JwtService.class);
        filterChain = mock(FilterChain.class);
        Set<String> urlWithoutAuthentication = Set.of("/public", "/ping");
        authenticationFilter = new AuthenticationFilter(jwtService, urlWithoutAuthentication);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        String token = "validToken";
        String email = "user@example.com";

        when(jwtService.validateTokenExpiration(token)).thenReturn(true);
        when(jwtService.getUserEmail(token)).thenReturn(email);

        request.addHeader("Authorization", "Bearer " + token);

        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(jwtService, times(1)).validateTokenExpiration(token);
        verify(jwtService, times(1)).getUserEmail(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        String token = "invalidToken";
        when(jwtService.validateTokenExpiration(token)).thenReturn(false);

        request.addHeader("Authorization", "Bearer " + token);

        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void testDoFilterInternalWithOtherToken() throws ServletException, IOException {
        request.addHeader("Authorization", "other");

        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void testDoFilterInternalWithoutToken() throws ServletException, IOException {
        String url = "/public";
        request.setRequestURI(url);
        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("anonymous", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWithUrlWithoutAuthentication() throws ServletException, IOException {
        String url = "/public";
        request.setRequestURI(url);

        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("anonymous", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
