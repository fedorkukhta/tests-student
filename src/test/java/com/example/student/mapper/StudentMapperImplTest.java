package com.example.student.mapper;

import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponseList;
import com.example.student.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentMapperImplTest {
    private final StudentMapperImpl mapper = new StudentMapperImpl();

    @Test
    void mapsRequestAndResponses() {
        StudentRequest request = new StudentRequest("Ann", "Smith", "123", "ann@example.com");
        Student student = mapper.requestToStudent(request);
        student.setId(7L);

        assertEquals("Ann", student.getName());
        assertEquals("Smith", student.getSurname());
        assertEquals("123", student.getPhoneNumber());
        assertEquals("ann@example.com", student.getEmail());
        assertEquals(7L, mapper.studentToFullResponse(student).getId());
        assertEquals("Ann", mapper.studentToResponse(student).getName());
        assertEquals("Smith", mapper.studentToStudentResponseForList(student).getSurname());
    }

    @Test
    void mapsStudentLists() {
        Student first = new Student();
        first.setId(1L);
        first.setName("Ann");
        Student second = new Student();
        second.setId(2L);
        second.setName("Bob");

        assertEquals(2, mapper.studentListToListOfStudentResponse(List.of(first, second)).size());
        StudentResponseList response = mapper.studentListToStudentResponseList(List.of(first, second));
        assertEquals(2, response.getTotalCount());
        assertEquals(2, response.getResponse().size());
    }
}
