package com.taskscheduler.auth.controller;

import com.taskscheduler.auth.dto.ApiResponse;
import com.taskscheduler.auth.dto.UserDTO;
import com.taskscheduler.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, String>>> register(@RequestBody UserDTO userDTO) {
        String token = authService.register(userDTO);
        return ResponseEntity.ok(ApiResponse.success(
            "User registered successfully",
            Map.of("token", token)
        ));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody Map<String, String> credentials) {
        String token = authService.login(
            credentials.get("username"),
            credentials.get("password")
        );
        return ResponseEntity.ok(ApiResponse.success(
            "Login successful",
            Map.of("token", token)
        ));
    }
    
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.error("Invalid token format"));
        }
        
        String token = authHeader.substring(7);
        Map<String, Object> validationResult = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success(validationResult));
    }
}
