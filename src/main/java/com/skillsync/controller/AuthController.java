package com.skillsync.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.dto.AuthRequest;
import com.skillsync.dto.AuthResponse;
import com.skillsync.dto.GoogleAuthRequest;
import com.skillsync.entity.User;
import com.skillsync.repo.UserRepository;
import com.skillsync.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName()); // Set name from request
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // Google signup and login use the same logic: verify token, create user if not exists, return JWT
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleSignup(@RequestBody GoogleAuthRequest request) {
        return handleGoogleAuth(request);
    }

    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleAuthRequest request) {
        return handleGoogleAuth(request);
    }

    // Shared logic for Google signup/login
    private ResponseEntity<AuthResponse> handleGoogleAuth(GoogleAuthRequest request) {
        try {
            // 1. Verify the Google ID token
            String idToken = request.getCredential();
            if (!StringUtils.hasText(idToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(tokenInfoUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(result);
            String email = json.get("email").asText();
            String name = json.has("name") ? json.get("name").asText() : email;
            // 2. Check if user exists
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPassword(""); // No password for Google users
                userRepository.save(user);
            }
            // 3. Issue JWT
            String token = jwtService.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
