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

import org.springframework.security.access.AccessDeniedException;


@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

        public ActivityResponse trackActivity(ActivityRequest request) {
        User user =userRepository.findById(request.getUserId())
                .orElseThrow(()-> new RuntimeException("Invaild User"+request.getUserId()));
        Activity activity = Activity.builder()
                .user(user)
                .type(request.getType())
                .duretion(request.getDuration())
                .startTime(request.getStartTime())
                .additionalMetrix(request.getAdditionalMetrics())
                .caloriesburned(request.getCaloriesBurned())
                .build();
      Activity savedActivity =  activityRepository.save(activity);
      return mapToResponse(savedActivity);
    }

        public ActivityResponse trackActivityForUser(String userId, ActivityRequest request) {
            User user =userRepository.findById(userId)
                    .orElseThrow(()-> new RuntimeException("Invaild User"+userId));
            Activity activity = Activity.builder()
                    .user(user)
                    .type(request.getType())
                    .duretion(request.getDuration())
                    .startTime(request.getStartTime())
                    .additionalMetrix(request.getAdditionalMetrics())
                    .caloriesburned(request.getCaloriesBurned())
                    .build();
            Activity savedActivity =  activityRepository.save(activity);
            return mapToResponse(savedActivity);
        }


    private ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(savedActivity.getId());
        response.setUserId(savedActivity.getUser().getId());
        response.setType(savedActivity.getType());
        response.setDuration(savedActivity.getDuretion());
        response.setCaloriesBurned(savedActivity.getCaloriesburned());
        response.setStartTime(savedActivity.getStartTime());
        response.setAdditionalMetrics(savedActivity.getAdditionalMetrix());
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

    public ActivityResponse getByIdForUser(String id, String userId, boolean isAdmin) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + id));

        if (!isAdmin && !activity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to view this activity");
        }

        return mapToResponse(activity);
    }

    public ActivityResponse updateForUser(String id, String userId, boolean isAdmin, ActivityRequest request) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + id));

        if (!isAdmin && !activity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to update this activity");
        }

        // update fields
        activity.setType(request.getType());
        activity.setDuretion(request.getDuration());
        activity.setCaloriesburned(request.getCaloriesBurned());
        activity.setStartTime(request.getStartTime());
        activity.setAdditionalMetrix(request.getAdditionalMetrics());

        Activity saved = activityRepository.save(activity);

        return mapToResponse(saved);
    }
    public void deleteForUser(String id, String userId, boolean isAdmin) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + id));

        if (!isAdmin && !activity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to delete this activity");
        }

        activityRepository.delete(activity);
    }
}
