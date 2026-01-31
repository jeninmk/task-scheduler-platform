package com.taskscheduler.auth.service;

import com.taskscheduler.auth.entity.User;
import com.taskscheduler.auth.repository.UserRepository;
import com.taskscheduler.auth.util.JwtUtil;
import com.taskscheduler.common.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public String register(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        
        userRepository.save(user);
        
        return jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
    }
    
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        return jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
    }
    
    public Map<String, Object> validateToken(String token) {
        Map<String, Object> result = new HashMap<>();
        
        if (!jwtUtil.validateToken(token)) {
            result.put("valid", false);
            result.put("message", "Invalid token");
            return result;
        }
        
        result.put("valid", true);
        result.put("username", jwtUtil.extractUsername(token));
        result.put("userId", jwtUtil.extractUserId(token));
        return result;
    }
}
