package com.example.student.controller;

import com.example.student.dto.AuthResponse;
import com.example.student.dto.LoginRequest;
import com.example.student.model.Role;
import com.example.student.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Test
    void authenticatesAndReturnsTokenWithRoles() {
        AuthenticationManager manager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        UserDetails user = User.withUsername("user@example.com").password("x")
                .authorities("ROLE_ADMIN", "ROLE_TEACHER").build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken("user@example.com", Set.of(Role.ADMIN, Role.TEACHER))).thenReturn("jwt");

        AuthResponse response = new AuthController(manager, jwtService)
                .login(new LoginRequest("user@example.com", "secret"));

        assertEquals("jwt", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(3600, response.getExpiresIn());
        assertEquals(2, response.getRoles().size());
    }
}
