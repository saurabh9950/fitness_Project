package com.project.fitness_project.service;

import com.project.fitness_project.dto.RecommendationRequest;
import com.project.fitness_project.dto.RecommendationResponse;
import com.project.fitness_project.model.Activity;
import com.project.fitness_project.model.Recommendation;
import com.project.fitness_project.model.User;
import com.project.fitness_project.repository.ActivityRepository;
import com.project.fitness_project.repository.RecommendationRepository;
import com.project.fitness_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final AiRecommendationService aiRecommendationService;
    public  RecommendationResponse generatRecommend(RecommendationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()-> new RuntimeException("Invalid User"+request.getUserId()));

        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(()-> new RuntimeException("Invalid activityUser"+request.getActivityId()));
        Recommendation recommendation = Recommendation.builder()
                .user(user)
                .activity(activity)
                .improvements(request.getImprovements())
                .suggestions(request.getSuggestions())
                .safety(request.getSafety())
                .build();

        Recommendation savedRecommendation =
                recommendationRepository.save(recommendation);

        return mapToResponse(savedRecommendation);
    }

    // PREVIEW ONLY: generate recommendation with AI without saving to DB
    public RecommendationResponse generatRecommendForUser(String userId, RecommendationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("Invalid User"+userId));

        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(()-> new RuntimeException("Invalid activityUser"+request.getActivityId()));

        AiRecommendationService.AiResult aiResult = aiRecommendationService.generateFor(activity, user);

        Recommendation recommendation = Recommendation.builder()
                .user(user)
                .activity(activity)
                .improvements(aiResult.getImprovements())
                .suggestions(aiResult.getSuggestions())
                .safety(aiResult.getSafety())
                .build();

        // No save here – this is just a preview
        return mapToResponse(recommendation);
    }

    // SAVE: user accepted the recommendation and wants to persist it
    public RecommendationResponse saveRecommendationForUser(String userId, RecommendationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("Invalid User"+userId));

        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(()-> new RuntimeException("Invalid activityUser"+request.getActivityId()));
        Recommendation recommendation = Recommendation.builder()
                .user(user)
                .activity(activity)
                .improvements(request.getImprovements())
                .suggestions(request.getSuggestions())
                .safety(request.getSafety())
                .build();

        Recommendation savedRecommendation =
                recommendationRepository.save(recommendation);

        return mapToResponse(savedRecommendation);
    }

    private RecommendationResponse mapToResponse(Recommendation savedRecommendation) {
        RecommendationResponse response = new RecommendationResponse();
        response.setId(savedRecommendation.getId());
        response.setUserId(savedRecommendation.getUser().getId());
        response.setActivityId(savedRecommendation.getActivity().getId());
        response.setImprovements(savedRecommendation.getImprovements());
        response.setSuggestions(savedRecommendation.getSuggestions());
        response.setSafety(savedRecommendation.getSafety());
        return response;
    }

    public List<RecommendationResponse> getRecommend(String userId) {
        List<Recommendation> list = recommendationRepository.findByUserId(userId);
        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RecommendationResponse> getRecommendActivity(String activityId) {
        List<Recommendation> list = recommendationRepository.findByActivityId(activityId);
        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteForUser(String id, String userId, boolean isAdmin) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation not found: " + id));

        if (!isAdmin && !recommendation.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to delete this recommendation");
        }

        recommendationRepository.delete(recommendation);
    }
}
