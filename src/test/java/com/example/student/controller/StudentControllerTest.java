package com.example.student.controller;

import com.example.student.dto.*;
import com.example.student.service.StudentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class StudentControllerTest {
    private final StudentService service = mock(StudentService.class);
    private final StudentController controller = new StudentController(service);

    @Test
    void delegatesStudentOperations() {
        StudentRequest request = new StudentRequest();
        StudentUpdateRequest update = new StudentUpdateRequest();
        StudentResponse response = new StudentResponse();
        StudentFullResponse full = new StudentFullResponse();
        StudentResponseList list = new StudentResponseList(java.util.List.of(), 0);
        when(service.findAll()).thenReturn(list);
        when(service.findById(1L)).thenReturn(full);
        when(service.save(request)).thenReturn(response);
        when(service.update(1L, update)).thenReturn(response);

        assertSame(list, controller.findAll());
        assertSame(full, controller.findStudentById(1L));
        assertSame(full, controller.getFullInfoById(1L));
        assertSame(response, controller.createStudent(request));
        assertSame(response, controller.updateStudent(1L, update));
        controller.deleteStudentById(1L);
        verify(service).deleteById(1L);
    }
}
