package com.example.student.configuration;

import com.example.student.model.User;
import com.example.student.repository.StudentRepository;
import com.example.student.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppInitializerTest {
    @Test
    void createsDefaultUsersAndStudentsWhenDatabaseIsEmpty() {
        UserRepository users = mock(UserRepository.class);
        StudentRepository students = mock(StudentRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(users.existsByEmail(any())).thenReturn(false);
        when(encoder.encode(any())).thenReturn("encoded");
        when(students.count()).thenReturn(0L);

        new AppInitializer(users, encoder, students).init();

        verify(users, times(3)).save(any(User.class));
        verify(students, times(5)).save(any());
    }

    @Test
    void doesNotCreateDefaultsWhenRecordsAlreadyExist() {
        UserRepository users = mock(UserRepository.class);
        StudentRepository students = mock(StudentRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(users.existsByEmail(any())).thenReturn(true);
        when(students.count()).thenReturn(2L);

        new AppInitializer(users, encoder, students).init();

        verify(users, never()).save(any());
        verify(students, never()).save(any());
        verifyNoInteractions(encoder);
    }
}
