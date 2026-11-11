package com.example.student.configuration;

import com.example.student.security.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigurationTest {
    @Test
    void createsPasswordEncoderAndAuthenticationManager() throws Exception {
        SecurityConfiguration configuration = new SecurityConfiguration(mock(JwtFilter.class));
        PasswordEncoder encoder = configuration.passwordEncoder();

        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
        assertTrue(encoder.matches("secret", encoder.encode("secret")));
    }
}
