package com.project.fitness_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.fitness_project.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
}
