package com.project.fitness_project.controller;

import com.project.fitness_project.dto.RecommendationRequest;
import com.project.fitness_project.dto.RecommendationResponse;
import com.project.fitness_project.model.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.project.fitness_project.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    @PostMapping("/generate")
    public ResponseEntity<RecommendationResponse> GenerateRecommend(@RequestBody RecommendationRequest request){
        return ResponseEntity.ok(recommendationService.generatRecommend(request));
    }



    @GetMapping("/User/{userId}")
    public ResponseEntity<List<RecommendationResponse>> GetRecommend(@PathVariable String userId ){
        return ResponseEntity.ok(recommendationService.getRecommend(userId));
    }
    @GetMapping("/Activity/{activityId}")
    public ResponseEntity<List<RecommendationResponse>> GetRecommendActivity(@PathVariable String activityId ){
        return ResponseEntity.ok(recommendationService.getRecommendActivity(activityId));
    }
}
