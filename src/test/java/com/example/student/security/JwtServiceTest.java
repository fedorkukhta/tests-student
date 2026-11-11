package com.example.student.security;

import com.example.student.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private final JwtService service = new JwtService();

    @Test
    void generatesAndReadsToken() {
        String token = service.generateToken("user@example.com", Set.of(Role.ADMIN, Role.TEACHER));

        assertEquals("user@example.com", service.extractUsername(token));
        assertEquals(Set.of(Role.ADMIN, Role.TEACHER), service.extractRoles(token));
        assertTrue(service.validateToken(token,
                User.withUsername("user@example.com").password("x").authorities("ROLE_ADMIN").build()));
        assertFalse(service.validateToken(token,
                User.withUsername("other@example.com").password("x").authorities("ROLE_ADMIN").build()));
    }

    @Test
    void rejectsMalformedToken() {
        assertThrows(RuntimeException.class, () -> service.extractUsername("not-a-jwt"));
    }
}
