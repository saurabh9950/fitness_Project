package com.project.fitness_project.controller;

import com.project.fitness_project.dto.RecommendationRequest;
import com.project.fitness_project.dto.RecommendationResponse;
import com.project.fitness_project.model.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.project.fitness_project.service.RecommendationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    @PostMapping("/generate")
        public ResponseEntity<RecommendationResponse> GenerateRecommend(@RequestBody RecommendationRequest request,
                                                                        Authentication auth){
            String userId = (String) auth.getPrincipal();
            return ResponseEntity.ok(recommendationService.generatRecommendForUser(userId, request));
    }



        @GetMapping("/User/{userId}")
        public ResponseEntity<List<RecommendationResponse>> GetRecommend(@PathVariable String userId,
                                                                         Authentication auth ){
            String authUserId = (String) auth.getPrincipal();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin && !authUserId.equals(userId)) {
                throw new AccessDeniedException("You are not allowed to view recommendations for this user");
            }
            return ResponseEntity.ok(recommendationService.getRecommend(userId));
    }
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<RecommendationResponse>> getRecommendActivity(
            @PathVariable("activityId") String activityId)
    {
        return ResponseEntity.ok(recommendationService.getRecommendActivity(activityId));
    }
}
