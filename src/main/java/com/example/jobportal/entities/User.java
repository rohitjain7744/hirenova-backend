package com.example.jobportal.entities;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name = "profile_image")
    private String ProfileImage;

    // No-args constructor
    public User() {
    }

    // All-args constructor
    public User(Long id, String name, String email, String password, Role role , String profileImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.ProfileImage =profileImage;
    }

    // ===== Getters =====

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
    
    public String getProfileImage() {
    	return ProfileImage;
    }

    // ===== Setters =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public void setProfileImage(String profileimage) {
    	this.ProfileImage =profileimage;
    }
}