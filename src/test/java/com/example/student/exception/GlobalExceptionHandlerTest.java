package com.example.student.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    @Test
    void mapsNotFoundExceptionToErrorResponse() {
        ResponseEntity<AppError> response = new GlobalExceptionHandler()
                .catchEntityNotFoundException(new EntityNotFoundException("missing"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("missing", response.getBody().getMessage());
    }
}
