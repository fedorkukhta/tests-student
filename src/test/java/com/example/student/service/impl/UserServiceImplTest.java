package com.example.student.service.impl;

import com.example.student.dto.user.UserRequest;
import com.example.student.dto.user.UserResponse;
import com.example.student.dto.user.UserResponseList;
import com.example.student.exception.EmailAlreadyExistsException;
import com.example.student.exception.EntityNotFoundException;
import com.example.student.mapper.UserMapper;
import com.example.student.model.Role;
import com.example.student.model.User;
import com.example.student.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock UserRepository repository;
    @Mock UserMapper mapper;
    @Mock PasswordEncoder encoder;
    @InjectMocks UserServiceImpl service;

    @Test
    void checksEmailAndRegistersUser() {
        UserRequest request = new UserRequest("user@example.com", "secret", Set.of(Role.ADMIN));
        User user = User.builder().email(request.getEmail()).password(request.getPassword()).build();
        when(mapper.toEntity(request)).thenReturn(user);
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode("secret")).thenReturn("encoded");

        assertTrue(service.register(request));
        assertEquals("encoded", user.getPassword());
        verify(repository).save(user);
        assertFalse(service.register(new UserRequest(null, "x", Set.of())));
    }

    @Test
    void rejectsInvalidRegistrationAndFindsUsers() {
        UserRequest request = new UserRequest("user@example.com", "secret", Set.of());
        User existing = User.builder().email(request.getEmail()).password("secret").build();
        when(mapper.toEntity(request)).thenReturn(existing);
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);
        assertFalse(service.register(request));

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(existing));
        assertSame(existing, service.findByEmail(request.getEmail()));
        when(repository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.findByEmail("missing@example.com"));
    }

    @Test
    void findsSavesUpdatesAndDeletes() {
        UserRequest request = new UserRequest("user@example.com", "secret", Set.of(Role.TEACHER));
        User user = User.builder().id(2L).email(request.getEmail()).password("old").roles(Set.of()).build();
        UserResponse response = new UserResponse(2L, request.getEmail(), request.getRoles());
        UserResponseList responses = new UserResponseList(List.of(response), 1);
        when(repository.findAll()).thenReturn(List.of(user));
        when(mapper.toListResponse(List.of(user))).thenReturn(responses);
        assertSame(responses, service.findAll());

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode("secret")).thenReturn("encoded");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(User.class))).thenReturn(response);
        assertSame(response, service.save(request));
        verify(encoder).encode("secret");

        when(repository.existsByEmail(request.getEmail())).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> service.save(request));

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        assertNull(service.updateUser(request));
        assertEquals(request.getRoles(), user.getRoles());
        service.deleteById(2L);
        verify(repository).deleteById(2L);
    }

    @Test
    void registrationAllowsBlankPasswordWithoutEncoding() {
        UserRequest request = new UserRequest("blank@example.com", " ", Set.of());
        User user = User.builder().email(request.getEmail()).password(" ").build();
        when(mapper.toEntity(request)).thenReturn(user);
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);

        assertTrue(service.register(request));
        verify(encoder, never()).encode(anyString());
    }
}
