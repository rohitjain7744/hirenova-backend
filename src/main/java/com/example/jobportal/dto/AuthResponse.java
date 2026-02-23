package com.example.jobportal.dto;

public class AuthResponse {

    private String message;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String profileImage;   // 🔥 Added

    // ✅ Constructor for successful login
    public AuthResponse(String message,
                        Long userId,
                        String name,
                        String email,
                        String role,
                        String profileImage) {

        this.message = message;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImage = profileImage;
    }

    // ✅ Constructor for simple message (like error)
    public AuthResponse(String message) {
        this.message = message;
    }

    // ===== Getters =====

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getProfileImage() {
        return profileImage;
    }
}