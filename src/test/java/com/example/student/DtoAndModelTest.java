package com.example.student;

import com.example.student.dto.*;
import com.example.student.dto.user.UserRequest;
import com.example.student.dto.user.UserResponse;
import com.example.student.dto.user.UserResponseList;
import com.example.student.exception.AppError;
import com.example.student.model.Role;
import com.example.student.model.Student;
import com.example.student.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoAndModelTest {
    @Test
    void exercisesStudentDtosAndEntity() {
        StudentRequest request = new StudentRequest();
        request.setName("Ann");
        request.setSurname("Smith");
        request.setPhoneNumber("123");
        request.setEmail("ann@example.com");
        assertEquals("Ann", request.getName());
        assertEquals("Smith", request.getSurname());
        assertEquals("123", request.getPhoneNumber());
        assertEquals("ann@example.com", request.getEmail());

        StudentUpdateRequest update = new StudentUpdateRequest("456", "new@example.com");
        assertEquals("456", update.getPhoneNumber());
        assertEquals("new@example.com", update.getEmail());
        Student student = new Student();
        student.setId(1L);
        student.setName("Ann");
        student.setSurname("Smith");
        student.setPhoneNumber("123");
        student.setEmail("ann@example.com");
        assertEquals(1L, student.getId());
        assertEquals("Ann", student.getName());
        assertEquals("Smith", student.getSurname());
        assertEquals("123", student.getPhoneNumber());
        assertEquals("ann@example.com", student.getEmail());

        StudentFullResponse full = new StudentFullResponse(1L, "Ann", "Smith", "123", "ann@example.com");
        StudentResponse shortResponse = new StudentResponse(1L, "Ann", "Smith");
        StudentResponseForList listResponse = new StudentResponseForList(1L, "Ann", "Smith");
        StudentResponseList list = new StudentResponseList(List.of(listResponse), 1);
        assertEquals("Ann", full.getName());
        assertEquals("Ann", shortResponse.getName());
        assertEquals("Ann", listResponse.getName());
        assertEquals(1, list.getTotalCount());
        assertNotNull(list.toString());
    }

    @Test
    void exercisesUserDtosEntityAndErrors() {
        UserRequest request = new UserRequest("user@example.com", "secret", Set.of(Role.ADMIN));
        assertEquals("user@example.com", request.getEmail());
        assertEquals("secret", request.getPassword());
        assertEquals(Set.of(Role.ADMIN), request.getRoles());
        UserResponse response = new UserResponse(1L, request.getEmail(), request.getRoles());
        UserResponseList list = new UserResponseList(List.of(response), 1);
        assertEquals(1L, response.getId());
        assertEquals("user@example.com", response.getEmail());
        assertEquals(Set.of(Role.ADMIN), response.getRoles());
        assertEquals(1, list.getTotalCount());

        User user = User.builder().id(1L).email("user@example.com").password("encoded")
                .roles(Set.of(Role.ADMIN)).build();
        assertEquals(1L, user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("encoded", user.getPassword());
        assertEquals(Set.of(Role.ADMIN), user.getRoles());
        assertNotNull(user.toString());

        AppError error = new AppError(400, "bad");
        assertEquals(400, error.getStatusCode());
        assertEquals("bad", error.getMessage());
        assertEquals(Role.TEACHER, Role.valueOf("TEACHER"));
    }
}
