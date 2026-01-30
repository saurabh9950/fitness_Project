package com.project.fitness_project.dto;
import com.project.fitness_project.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    private String id;
    private String userid;
    private ActivityType type;
    private Map<String,Object> additionalMetrix;
    private Integer duretion;
    private Integer caloriesburned;
    private LocalDateTime startTime;
    private LocalDateTime CreatedAt;
    private LocalDateTime UpdatedAt;



}
