package com.project.fitness_project.repository;

import com.project.fitness_project.model.Activity;
import com.project.fitness_project.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation ,String> {
    List<Recommendation> findByUserId(String userId);

    List<Recommendation> findByActivityId(String ActivityId);
}
