package com.example.student.service;

import com.example.student.model.Role;
import com.example.student.model.User;
import com.example.student.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock UserRepository repository;

    @Test
    void loadsUserWithRoleAuthorities() {
        User user = User.builder().email("user@example.com").password("encoded")
                .roles(Set.of(Role.ADMIN, Role.TEACHER)).build();
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        var details = new CustomUserDetailsService(repository).loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), details.getUsername());
        assertEquals("encoded", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER")));
    }

    @Test
    void throwsForMissingUser() {
        when(repository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> new CustomUserDetailsService(repository).loadUserByUsername("missing@example.com"));
    }
}
