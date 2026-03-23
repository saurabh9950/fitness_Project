package com.project.fitness_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness_project.model.Activity;
import com.project.fitness_project.model.User;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiRecommendationService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiRecommendationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Data
    public static class AiResult {
        private List<String> improvements;
        private List<String> suggestions;
        private List<String> safety;
    }

    public AiResult generateFor(Activity activity, User user) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set");
        }

        String prompt = buildPrompt(activity, user);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4.1-mini");
        body.put("temperature", 0.7);

        Map<String, String> systemMessage = Map.of(
                "role", "system",
                "content", "You are an expert fitness coach. " +
                        "Given a user's workout activity, you must respond ONLY with strict JSON " +
                        "in the format: {\"improvements\":[],\"suggestions\":[],\"safety\":[]} " +
                        "where each array contains short bullet-style strings in English."
        );

        Map<String, String> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );

        body.put("messages", List.of(systemMessage, userMessage));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map<?, ?> response = restTemplate.postForObject(OPENAI_API_URL, entity, Map.class);
        if (response == null || !response.containsKey("choices")) {
            throw new IllegalStateException("AI response is empty or invalid");
        }

        Object choicesObj = response.get("choices");
        if (!(choicesObj instanceof List<?> choicesList) || choicesList.isEmpty()) {
            throw new IllegalStateException("AI response has no choices");
        }

        Object firstChoice = choicesList.get(0);
        if (!(firstChoice instanceof Map<?, ?> choiceMap)) {
            throw new IllegalStateException("AI choice is invalid");
        }

        Object messageObj = choiceMap.get("message");
        if (!(messageObj instanceof Map<?, ?> messageMap)) {
            throw new IllegalStateException("AI message is invalid");
        }

        Object contentObj = messageMap.get("content");
        if (!(contentObj instanceof String content)) {
            throw new IllegalStateException("AI content is missing");
        }

        try {
            return objectMapper.readValue(content, AiResult.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI JSON content: " + content, e);
        }
    }

    private String buildPrompt(Activity activity, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("User: ")
                .append(user.getFirstName() != null ? user.getFirstName() : "")
                .append(" ")
                .append(user.getLastName() != null ? user.getLastName() : "")
                .append("\n");

        sb.append("Activity type: ")
                .append(activity.getType() != null ? activity.getType().name() : "UNKNOWN")
                .append("\n");

        sb.append("Duration (minutes): ")
                .append(activity.getDuretion() != null ? activity.getDuretion() : 0)
                .append("\n");

        sb.append("Calories burned: ")
                .append(activity.getCaloriesburned() != null ? activity.getCaloriesburned() : 0)
                .append("\n");

        sb.append("Start time: ")
                .append(activity.getStartTime() != null ? activity.getStartTime() : "N/A")
                .append("\n");

        if (activity.getAdditionalMetrix() != null && !activity.getAdditionalMetrix().isEmpty()) {
            sb.append("Additional metrics: ").append(activity.getAdditionalMetrix().toString()).append("\n");
        }

        sb.append("Please analyze this workout and provide:\n")
                .append("- concrete improvements to performance or technique\n")
                .append("- helpful suggestions for future workouts\n")
                .append("- important safety tips\n")
                .append("Respond ONLY with valid JSON as previously described.");

        return sb.toString();
    }
}



