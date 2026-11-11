package com.example.student.mapper;

import com.example.student.dto.user.UserRequest;
import com.example.student.dto.user.UserResponseList;
import com.example.student.model.Role;
import com.example.student.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperImplTest {
    private final UserMapperImpl mapper = new UserMapperImpl();

    @Test
    void mapsEntityAndRequest() {
        UserRequest request = new UserRequest("user@example.com", "secret", Set.of(Role.ADMIN));
        User user = mapper.toEntity(request);
        user.setId(4L);

        assertEquals("user@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals(Set.of(Role.ADMIN), user.getRoles());
        assertEquals(4L, mapper.toResponse(user).getId());
    }

    @Test
    void mapsLists() {
        User user = User.builder().id(1L).email("a@example.com").roles(Set.of(Role.TEACHER)).build();
        UserResponseList response = mapper.toListResponse(List.of(user));

        assertEquals(1, response.getTotalCount());
        assertEquals("a@example.com", response.getResponses().getFirst().getEmail());
        assertEquals(1, mapper.toResponseList(List.of(user)).size());
    }
}
