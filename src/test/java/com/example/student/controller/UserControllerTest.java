package com.example.student.controller;

import com.example.student.dto.user.UserRequest;
import com.example.student.dto.user.UserResponse;
import com.example.student.dto.user.UserResponseList;
import com.example.student.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Test
    void delegatesUserOperations() {
        UserService service = mock(UserService.class);
        UserController controller = new UserController(service);
        UserRequest request = new UserRequest();
        UserResponse response = new UserResponse();
        UserResponseList list = new UserResponseList(List.of(response), 1);
        when(service.findAll()).thenReturn(list);
        when(service.save(request)).thenReturn(response);
        when(service.updateUser(request)).thenReturn(response);

        assertSame(list, controller.getAllUsers());
        assertSame(response, controller.save(request));
        assertSame(response, controller.updateUser(request));
        controller.deleteById(5L);
        verify(service).deleteById(5L);
    }
}
