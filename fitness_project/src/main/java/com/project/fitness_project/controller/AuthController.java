package com.project.fitness_project.controller;

import com.project.fitness_project.Security.JwtUtils;
import com.project.fitness_project.dto.LoginRequest;
import com.project.fitness_project.dto.LoginResponse;
import com.project.fitness_project.dto.RegisterRequest;
import com.project.fitness_project.dto.UserResponse;
import com.project.fitness_project.model.User;
import com.project.fitness_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.fitness_project.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    @PostMapping("/register")
    public ResponseEntity<UserResponse> Register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(userService.register(registerRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> Login(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            User user = userRepository.findByEmail(loginRequest.getEmail());
            if(user==null) return ResponseEntity.status(401).build();
            if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
                return ResponseEntity.status(401).build();
            }
            String token = jwtUtils.generateToken(user.getId(),user.getRole().name());
            return ResponseEntity.ok(new LoginResponse(
                    token , userService.mapToResponse(user)
            ));
        }
        catch (AuthenticationException e){
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }

    }
}
