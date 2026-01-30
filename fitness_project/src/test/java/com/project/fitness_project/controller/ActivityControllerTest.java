package com.project.fitness_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness_project.dto.ActivityRequest;
import com.project.fitness_project.dto.ActivityResponse;
import com.project.fitness_project.model.ActivityType;
import com.project.fitness_project.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService activityService;

    @Autowired
    private ObjectMapper objectMapper;

    private ActivityRequest testRequest;
    private ActivityResponse testResponse;

    @BeforeEach
    void setUp() {
        Map<String, Object> additionalMetrics = new HashMap<>();
        additionalMetrics.put("distance", 5.0);
        additionalMetrics.put("pace", "6:00");

        testRequest = new ActivityRequest();
        testRequest.setUserid("user-123");
        testRequest.setType(ActivityType.RUNNING);
        testRequest.setDuretion(30);
        testRequest.setStartTime(LocalDateTime.now());
        testRequest.setAdditionalMetrix(additionalMetrics);

        testResponse = new ActivityResponse();
        testResponse.setId("activity-123");
        testResponse.setUserid("user-123");
        testResponse.setType(ActivityType.RUNNING);
        testResponse.setDuretion(30);
        testResponse.setCaloriesburned(300);
        testResponse.setStartTime(testRequest.getStartTime());
        testResponse.setAdditionalMetrix(additionalMetrics);
    }

    @Test
    void testTrackActivity_Success() throws Exception {
        // Arrange
        when(activityService.trackActivity(any(ActivityRequest.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/activity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("activity-123"))
                .andExpect(jsonPath("$.userid").value("user-123"))
                .andExpect(jsonPath("$.type").value("RUNNING"))
                .andExpect(jsonPath("$.duretion").value(30))
                .andExpect(jsonPath("$.caloriesburned").value(300));
    }

    @Test
    void testGetUserActivity_Success() throws Exception {
        // Arrange
        ActivityResponse response1 = new ActivityResponse();
        response1.setId("activity-1");
        response1.setUserid("user-123");
        response1.setType(ActivityType.RUNNING);
        response1.setDuretion(30);

        ActivityResponse response2 = new ActivityResponse();
        response2.setId("activity-2");
        response2.setUserid("user-123");
        response2.setType(ActivityType.CYCLING);
        response2.setDuretion(60);

        List<ActivityResponse> responses = Arrays.asList(response1, response2);
        when(activityService.getUserActivity("user-123")).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/activity")
                        .header("X-User-ID", "user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("activity-1"))
                .andExpect(jsonPath("$[1].id").value("activity-2"));
    }

    @Test
    void testGetUserActivity_EmptyList() throws Exception {
        // Arrange
        when(activityService.getUserActivity("user-123")).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/activity")
                        .header("X-User-ID", "user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

