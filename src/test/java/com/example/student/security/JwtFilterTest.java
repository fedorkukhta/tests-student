package com.example.student.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    @Mock UserDetailsService userDetailsService;
    @Mock JwtService jwtService;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain chain;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesValidBearerToken() throws Exception {
        UserDetails user = User.withUsername("user@example.com").password("x").authorities("ROLE_ADMIN").build();
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractUsername("token")).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.validateToken("token", user)).thenReturn(true);

        new JwtFilter(userDetailsService, jwtService).doFilter(request, response, chain);

        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(chain).doFilter(request, response);
    }

    @Test
    void skipsMissingHeaderAndExistingAuthentication() throws Exception {
        new JwtFilter(userDetailsService, jwtService).doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("existing", null));
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        new JwtFilter(userDetailsService, jwtService).doFilter(request, response, chain);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void returnsUnauthorizedForInvalidApiToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid");
        when(jwtService.extractUsername("invalid")).thenThrow(new IllegalArgumentException("bad token"));
        when(request.getRequestURI()).thenReturn("/api/v1/student");
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        new JwtFilter(userDetailsService, jwtService).doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(body.toString().contains("IllegalArgumentException"));
        verifyNoInteractions(chain);
    }

    @Test
    void rethrowsInvalidTokenOutsideApi() {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid");
        when(jwtService.extractUsername("invalid")).thenThrow(new IllegalArgumentException("bad token"));
        when(request.getRequestURI()).thenReturn("/health");

        assertThrows(IllegalArgumentException.class,
                () -> new JwtFilter(userDetailsService, jwtService).doFilter(request, response, chain));
    }
}
