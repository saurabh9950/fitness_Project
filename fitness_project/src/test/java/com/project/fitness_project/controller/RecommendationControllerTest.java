package com.project.fitness_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness_project.dto.RecommendationRequest;
import com.project.fitness_project.dto.RecommendationResponse;
import com.project.fitness_project.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecommendationRequest testRequest;
    private RecommendationResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = new RecommendationRequest();
        testRequest.setUserId("user-123");
        testRequest.setActivityId("activity-123");
        testRequest.setImprovements(Arrays.asList("Improve form", "Increase pace"));
        testRequest.setSuggestions(Arrays.asList("Try interval training"));
        testRequest.setSafety(Arrays.asList("Warm up properly"));

        testResponse = new RecommendationResponse();
        testResponse.setUserId("user-123");
        testResponse.setActivityId("activity-123");
        testResponse.setImprovements(testRequest.getImprovements());
        testResponse.setSuggestions(testRequest.getSuggestions());
        testResponse.setSafety(testRequest.getSafety());
    }

    @Test
    void testGeneraterecommend_Success() throws Exception {
        // Arrange
        when(recommendationService.generatRecommend(any(RecommendationRequest.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/recommendation/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.activityId").value("activity-123"))
                .andExpect(jsonPath("$.improvements").isArray())
                .andExpect(jsonPath("$.suggestions").isArray())
                .andExpect(jsonPath("$.safety").isArray());
    }

    @Test
    void testGetrecommend_Success() throws Exception {
        // Arrange
        RecommendationResponse response1 = new RecommendationResponse();
        response1.setUserId("user-123");
        response1.setActivityId("activity-1");
        response1.setImprovements(Arrays.asList("Improvement 1"));

        RecommendationResponse response2 = new RecommendationResponse();
        response2.setUserId("user-123");
        response2.setActivityId("activity-2");
        response2.setImprovements(Arrays.asList("Improvement 2"));

        List<RecommendationResponse> responses = Arrays.asList(response1, response2);
        when(recommendationService.getRecommend("user-123")).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/recommendation/User/{userId}", "user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));
    }

    @Test
    void testGetrecommend_EmptyList() throws Exception {
        // Arrange
        when(recommendationService.getRecommend("user-123")).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/recommendation/User/{userId}", "user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

