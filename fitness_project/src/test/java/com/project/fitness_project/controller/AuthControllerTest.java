package com.project.fitness_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness_project.dto.RegisterRequest;
import com.project.fitness_project.dto.UserResponse;
import com.project.fitness_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest testRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = new RegisterRequest();
        testRequest.setEmail("test@example.com");
        testRequest.setPassword("password123");
        testRequest.setFirstName("John");
        testRequest.setLastName("Doe");

        testResponse = new UserResponse();
        testResponse.setId("user-123");
        testResponse.setEmail("test@example.com");
        testResponse.setPassword("password123");
        testResponse.setFirstName("John");
        testResponse.setLastName("Doe");
        testResponse.setCreatedAt(LocalDateTime.now());
        testResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("user-123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testRegister_WithMissingFields() throws Exception {
        // Arrange
        RegisterRequest incompleteRequest = new RegisterRequest();
        incompleteRequest.setEmail("test@example.com");
        // Missing other fields

        UserResponse incompleteResponse = new UserResponse();
        incompleteResponse.setId("user-456");
        incompleteResponse.setEmail("test@example.com");

        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(incompleteResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}

