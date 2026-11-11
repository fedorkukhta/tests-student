package com.example.student.service.impl;

import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponse;
import com.example.student.dto.StudentResponseList;
import com.example.student.dto.StudentFullResponse;
import com.example.student.dto.StudentUpdateRequest;
import com.example.student.exception.EntityNotFoundException;
import com.example.student.mapper.StudentMapper;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {
    @Mock StudentRepository repository;
    @Mock StudentMapper mapper;
    @InjectMocks StudentServiceImpl service;

    @Test
    void savesAndFindsStudents() {
        StudentRequest request = new StudentRequest();
        Student student = new Student();
        StudentResponse saved = new StudentResponse(1L, "Ann", "Smith");
        when(mapper.requestToStudent(request)).thenReturn(student);
        when(repository.save(student)).thenReturn(student);
        when(mapper.studentToResponse(student)).thenReturn(saved);
        StudentFullResponse full = new StudentFullResponse();
        when(mapper.studentToFullResponse(student)).thenReturn(full);
        when(repository.findById(1L)).thenReturn(Optional.of(student));

        assertSame(saved, service.save(request));
        assertSame(full, service.findById(1L));
    }

    @Test
    void findsAllUpdatesAndDeletes() {
        Student student = new Student();
        StudentResponse response = new StudentResponse(1L, "Ann", "Smith");
        StudentResponseList list = new StudentResponseList(List.of(), 0);
        when(repository.findAll()).thenReturn(List.of(student));
        when(mapper.studentListToStudentResponseList(List.of(student))).thenReturn(list);
        when(repository.findById(1L)).thenReturn(Optional.of(student));
        when(repository.save(student)).thenReturn(student);
        when(mapper.studentToResponse(student)).thenReturn(response);

        assertSame(list, service.findAll());
        assertSame(response, service.update(1L, new StudentUpdateRequest("999", "new@example.com")));
        assertEquals("999", student.getPhoneNumber());
        assertEquals("new@example.com", student.getEmail());
        service.update(1L, new StudentUpdateRequest(null, null));
        service.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void throwsWhenStudentDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.findById(9L));
        assertTrue(exception.getMessage().contains("9"));
    }
}
