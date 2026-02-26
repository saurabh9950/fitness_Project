package com.project.fitness_project.controller;

import com.project.fitness_project.dto.UserResponse;
import com.project.fitness_project.model.UserRole;
import com.project.fitness_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable String id,
            @RequestParam("role") UserRole role
    ) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }
}


