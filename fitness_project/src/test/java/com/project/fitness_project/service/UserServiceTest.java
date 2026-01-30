package com.project.fitness_project.service;

import com.project.fitness_project.dto.RegisterRequest;
import com.project.fitness_project.dto.UserResponse;
import com.project.fitness_project.model.User;
import com.project.fitness_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private RegisterRequest testRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        testRequest = new RegisterRequest();
        testRequest.setEmail("test@example.com");
        testRequest.setPassword("password123");
        testRequest.setFirstName("John");
        testRequest.setLastName("Doe");

        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void testRegister_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponse response = userService.register(testRequest);

        // Assert
        assertNotNull(response);
        assertEquals("user-123", response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("password123", response.getPassword());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_WithNullFields() {
        // Arrange
        RegisterRequest requestWithNulls = new RegisterRequest();
        requestWithNulls.setEmail(null);
        requestWithNulls.setPassword(null);
        requestWithNulls.setFirstName(null);
        requestWithNulls.setLastName(null);

        User userWithNulls = User.builder()
                .id("user-456")
                .email(null)
                .password(null)
                .firstName(null)
                .lastName(null)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(userWithNulls);

        // Act
        UserResponse response = userService.register(requestWithNulls);

        // Assert
        assertNotNull(response);
        assertEquals("user-456", response.getId());
        assertNull(response.getEmail());
        assertNull(response.getPassword());
        assertNull(response.getFirstName());
        assertNull(response.getLastName());

        verify(userRepository, times(1)).save(any(User.class));
    }
}

