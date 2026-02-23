package com.example.jobportal.controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobportal.entities.User;
import com.example.jobportal.repositories.UserRepository;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================
    // UPDATE PROFILE
    // ==========================
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(updatedUser.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());

        return ResponseEntity.ok(userRepository.save(user));
    }

    // ==========================
    // UPLOAD PROFILE PHOTO
    // ==========================
    @PostMapping("/upload-photo/{id}")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file
    ) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Only image files allowed");
        }

        Path uploadPath = Paths.get("uploads/profile");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (user.getProfileImage() != null) {
            Path oldPath = uploadPath.resolve(user.getProfileImage());
            Files.deleteIfExists(oldPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.write(filePath, file.getBytes());

        user.setProfileImage(fileName);

        return ResponseEntity.ok(userRepository.save(user));
    }

    // ==========================
    // GET PROFILE IMAGE
    // ==========================
    @GetMapping("/profile-image/{fileName}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String fileName) throws IOException {

        Path path = Paths.get("uploads/profile").resolve(fileName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    // ==========================
    // CHANGE PASSWORD
    // ==========================
    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Password too short");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated");
    }

    // ==========================
    // DELETE ACCOUNT
    // ==========================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("User not found");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("Account deleted");
    }
}