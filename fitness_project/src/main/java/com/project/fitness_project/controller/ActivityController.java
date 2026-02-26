package com.project.fitness_project.controller;

import com.project.fitness_project.dto.ActivityRequest;
import com.project.fitness_project.dto.ActivityResponse;
import com.project.fitness_project.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
        @PostMapping
        public ResponseEntity<ActivityResponse> TrackActivity(@RequestBody ActivityRequest request,
                                                              Authentication auth){
            String userId = (String) auth.getPrincipal();
            return ResponseEntity.ok(activityService.trackActivityForUser(userId, request));
        }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivity(Authentication auth){
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(activityService.getUserActivity(userId));
    }

        @GetMapping("/{id}")
        public ResponseEntity<ActivityResponse> getById(@PathVariable String id, Authentication auth){
            String userId = (String) auth.getPrincipal();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return ResponseEntity.ok(activityService.getByIdForUser(id, userId, isAdmin));
    }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable String id, Authentication auth){
            String userId = (String) auth.getPrincipal();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            activityService.deleteForUser(id, userId, isAdmin);
            return ResponseEntity.noContent().build();
    }

        @PutMapping("/{id}")
        public ResponseEntity<ActivityResponse> update(
                @PathVariable String id,
                @RequestBody ActivityRequest req,
                Authentication auth){
            String userId = (String) auth.getPrincipal();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return ResponseEntity.ok(activityService.updateForUser(id, userId, isAdmin, req));
    }
}
