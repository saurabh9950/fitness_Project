package com.project.fitness_project.service;

import com.project.fitness_project.dto.ActivityRequest;
import com.project.fitness_project.dto.ActivityResponse;
import com.project.fitness_project.model.Activity;
import com.project.fitness_project.model.User;
import com.project.fitness_project.repository.ActivityRepository;
import com.project.fitness_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityResponse trackActivity(ActivityRequest request) {
        User user =userRepository.findById(request.getUserid())
                .orElseThrow(()-> new RuntimeException("Invaild User"+request.getUserid()));
        Activity activity = Activity.builder()
                .user(user)
                .type(request.getType())
                .duretion(request.getDuretion())
                .startTime(request.getStartTime())
                .additionalMetrix(request.getAdditionalMetrix())
                .build();
      Activity savedActivity =  activityRepository.save(activity);
      return mapToResponse(savedActivity);
    }


    private ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(savedActivity.getId());
        response.setUserid(savedActivity.getUser().getId());
        response.setType(savedActivity.getType());
        response.setDuretion(savedActivity.getDuretion());
        response.setCaloriesburned(savedActivity.getCaloriesburned());
        response.setStartTime(savedActivity.getStartTime());
        response.setAdditionalMetrix(savedActivity.getAdditionalMetrix());
        response.setCreatedAt(savedActivity.getCreatedAt());
        response.setUpdatedAt(savedActivity.getUpdatedAt());
        return response;
    }

    public List<ActivityResponse> getUserActivity(String userId) {
        List <Activity> list =activityRepository.findByUserId(userId);
        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
