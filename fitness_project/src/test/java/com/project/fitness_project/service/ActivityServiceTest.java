package com.project.fitness_project.service;

import com.project.fitness_project.dto.ActivityRequest;
import com.project.fitness_project.dto.ActivityResponse;
import com.project.fitness_project.model.Activity;
import com.project.fitness_project.model.ActivityType;
import com.project.fitness_project.model.User;
import com.project.fitness_project.repository.ActivityRepository;
import com.project.fitness_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivityService activityService;

    private User testUser;
    private Activity testActivity;
    private ActivityRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        Map<String, Object> additionalMetrics = new HashMap<>();
        additionalMetrics.put("distance", 5.0);
        additionalMetrics.put("pace", "6:00");

        testRequest = new ActivityRequest();
        testRequest.setUserid("user-123");
        testRequest.setType(ActivityType.RUNNING);
        testRequest.setDuretion(30);
        testRequest.setStartTime(LocalDateTime.now());
        testRequest.setAdditionalMetrix(additionalMetrics);

        testActivity = Activity.builder()
                .id("activity-123")
                .user(testUser)
                .type(ActivityType.RUNNING)
                .duretion(30)
                .caloriesburned(300)
                .startTime(testRequest.getStartTime())
                .additionalMetrix(additionalMetrics)
                .build();
    }

    @Test
    void testTrackActivity_Success() {
        // Arrange
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        // Act
        ActivityResponse response = activityService.trackActivity(testRequest);

        // Assert
        assertNotNull(response);
        assertEquals("activity-123", response.getId());
        assertEquals("user-123", response.getUserid());
        assertEquals(ActivityType.RUNNING, response.getType());
        assertEquals(30, response.getDuretion());
        assertEquals(300, response.getCaloriesburned());
        assertEquals(testRequest.getStartTime(), response.getStartTime());
        assertEquals(testRequest.getAdditionalMetrix(), response.getAdditionalMetrix());

        verify(userRepository, times(1)).findById("user-123");
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void testTrackActivity_InvalidUser() {
        // Arrange
        when(userRepository.findById("user-123")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.trackActivity(testRequest);
        });

        assertTrue(exception.getMessage().contains("Invaild User"));
        verify(userRepository, times(1)).findById("user-123");
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void testGetUserActivity_Success() {
        // Arrange
        Activity activity1 = Activity.builder()
                .id("activity-1")
                .user(testUser)
                .type(ActivityType.RUNNING)
                .duretion(30)
                .caloriesburned(300)
                .build();

        Activity activity2 = Activity.builder()
                .id("activity-2")
                .user(testUser)
                .type(ActivityType.CYCLING)
                .duretion(60)
                .caloriesburned(500)
                .build();

        List<Activity> activities = Arrays.asList(activity1, activity2);
        when(activityRepository.findByUserId("user-123")).thenReturn(activities);

        // Act
        List<ActivityResponse> responses = activityService.getUserActivity("user-123");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("activity-1", responses.get(0).getId());
        assertEquals("activity-2", responses.get(1).getId());
        assertEquals("user-123", responses.get(0).getUserid());
        assertEquals("user-123", responses.get(1).getUserid());
        verify(activityRepository, times(1)).findByUserId("user-123");
    }

    @Test
    void testGetUserActivity_EmptyList() {
        // Arrange
        when(activityRepository.findByUserId("user-123")).thenReturn(List.of());

        // Act
        List<ActivityResponse> responses = activityService.getUserActivity("user-123");

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(activityRepository, times(1)).findByUserId("user-123");
    }
}

