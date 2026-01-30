package com.project.fitness_project.service;

import com.project.fitness_project.dto.RecommendationRequest;
import com.project.fitness_project.dto.RecommendationResponse;
import com.project.fitness_project.model.Activity;
import com.project.fitness_project.model.ActivityType;
import com.project.fitness_project.model.Recommendation;
import com.project.fitness_project.model.User;
import com.project.fitness_project.repository.ActivityRepository;
import com.project.fitness_project.repository.RecommendationRepository;
import com.project.fitness_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private User testUser;
    private Activity testActivity;
    private Recommendation testRecommendation;
    private RecommendationRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        testActivity = Activity.builder()
                .id("activity-123")
                .user(testUser)
                .type(ActivityType.RUNNING)
                .duretion(30)
                .build();

        testRequest = new RecommendationRequest();
        testRequest.setUserId("user-123");
        testRequest.setActivityId("activity-123");
        testRequest.setImprovements(Arrays.asList("Improve form", "Increase pace"));
        testRequest.setSuggestions(Arrays.asList("Try interval training", "Add strength training"));
        testRequest.setSafety(Arrays.asList("Warm up properly", "Stay hydrated"));

        testRecommendation = Recommendation.builder()
                .id("recommendation-123")
                .user(testUser)
                .activity(testActivity)
                .improvements(testRequest.getImprovements())
                .suggestions(testRequest.getSuggestions())
                .safety(testRequest.getSafety())
                .build();
    }

    @Test
    void testGeneratRecommend_Success() {
        // Arrange
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById("activity-123")).thenReturn(Optional.of(testActivity));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(testRecommendation);

        // Act
        RecommendationResponse response = recommendationService.generatRecommend(testRequest);

        // Assert
        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("activity-123", response.getActivityId());
        assertEquals(testRequest.getImprovements(), response.getImprovements());
        assertEquals(testRequest.getSuggestions(), response.getSuggestions());
        assertEquals(testRequest.getSafety(), response.getSafety());

        verify(userRepository, times(1)).findById("user-123");
        verify(activityRepository, times(1)).findById("activity-123");
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void testGeneratRecommend_InvalidUser() {
        // Arrange
        when(userRepository.findById("user-123")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recommendationService.generatRecommend(testRequest);
        });

        assertTrue(exception.getMessage().contains("Invalid User"));
        verify(userRepository, times(1)).findById("user-123");
        verify(activityRepository, never()).findById(anyString());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testGeneratRecommend_InvalidActivity() {
        // Arrange
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById("activity-123")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recommendationService.generatRecommend(testRequest);
        });

        assertTrue(exception.getMessage().contains("Invalid activityUser"));
        verify(userRepository, times(1)).findById("user-123");
        verify(activityRepository, times(1)).findById("activity-123");
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testGetRecommend_Success() {
        // Arrange
        Recommendation recommendation1 = Recommendation.builder()
                .id("rec-1")
                .user(testUser)
                .activity(testActivity)
                .improvements(Arrays.asList("Improvement 1"))
                .suggestions(Arrays.asList("Suggestion 1"))
                .safety(Arrays.asList("Safety 1"))
                .build();

        Recommendation recommendation2 = Recommendation.builder()
                .id("rec-2")
                .user(testUser)
                .activity(testActivity)
                .improvements(Arrays.asList("Improvement 2"))
                .suggestions(Arrays.asList("Suggestion 2"))
                .safety(Arrays.asList("Safety 2"))
                .build();

        List<Recommendation> recommendations = Arrays.asList(recommendation1, recommendation2);
        when(recommendationRepository.findByUserId("user-123")).thenReturn(recommendations);

        // Act
        List<RecommendationResponse> responses = recommendationService.getRecommend("user-123");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("user-123", responses.get(0).getUserId());
        assertEquals("user-123", responses.get(1).getUserId());
        verify(recommendationRepository, times(1)).findByUserId("user-123");
    }

    @Test
    void testGetRecommend_EmptyList() {
        // Arrange
        when(recommendationRepository.findByUserId("user-123")).thenReturn(List.of());

        // Act
        List<RecommendationResponse> responses = recommendationService.getRecommend("user-123");

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(recommendationRepository, times(1)).findByUserId("user-123");
    }
}

