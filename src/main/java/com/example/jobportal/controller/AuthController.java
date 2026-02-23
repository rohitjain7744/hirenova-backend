package com.example.jobportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.jobportal.dto.AuthResponse;
import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.entities.User;
import com.example.jobportal.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================
    // REGISTER
    // ==========================
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {

        userService.register(user);

        return ResponseEntity.ok(
                new AuthResponse("User Registered Successfully")
        );
    }

    // ==========================
    // LOGIN
    // ==========================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("User not found"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid credentials"));
        }

        return ResponseEntity.ok(
                new AuthResponse(
                        "Login Successful",
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getProfileImage()   // 🔥 FIXED (image included)
                )
        );
    }
}