package com.project.fitness_project.service;

import com.project.fitness_project.dto.RegisterRequest;
import com.project.fitness_project.dto.UserResponse;
import com.project.fitness_project.model.User;
import com.project.fitness_project.model.UserRole;
import com.project.fitness_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request){
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return mapToResponse(user);
    }

    public UserResponse updateUserRole(String id, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setRole(role);
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public UserResponse mapToResponse(User savedUser) {
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        return response;
    }
}
