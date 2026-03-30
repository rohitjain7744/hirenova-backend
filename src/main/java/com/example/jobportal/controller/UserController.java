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
@CrossOrigin(origins = "*") // allow production + local
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

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (updatedUser.getEmail() == null || updatedUser.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            if (!user.getEmail().equals(updatedUser.getEmail())
                    && userRepository.existsByEmail(updatedUser.getEmail())) {
                return ResponseEntity.badRequest().body("Email already in use");
            }

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());

            return ResponseEntity.ok(userRepository.save(user));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Update failed: " + e.getMessage());
        }
    }

    // ==========================
    // UPLOAD PROFILE PHOTO
    // ==========================
    @PostMapping("/upload-photo/{id}")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) {

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ null + empty check
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is missing or empty");
            }

            // ✅ safe content-type check
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files allowed");
            }

            // ✅ create directory safely
            Path uploadPath = Paths.get("uploads/profile");
            Files.createDirectories(uploadPath);

            // ✅ delete old file (if exists)
            if (user.getProfileImage() != null) {
                Path oldPath = uploadPath.resolve(user.getProfileImage());
                Files.deleteIfExists(oldPath);
            }

            // ✅ unique filename
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // ✅ write file
            Files.write(filePath, file.getBytes());

            // ✅ update DB
            user.setProfileImage(fileName);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Upload successful",
                    "fileName", fileName,
                    "imageUrl", "/users/profile-image/" + fileName
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    // ==========================
    // GET PROFILE IMAGE
    // ==========================
    @GetMapping("/profile-image/{fileName}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String fileName) {

        try {
            Path path = Paths.get("uploads/profile").resolve(fileName).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // ==========================
    // CHANGE PASSWORD
    // ==========================
    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newPassword = request.get("newPassword");

            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest().body("Password too short");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return ResponseEntity.ok("Password updated");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Password update failed");
        }
    }

    // ==========================
    // DELETE ACCOUNT
    // ==========================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {

        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.badRequest().body("User not found");
            }

            userRepository.deleteById(id);
            return ResponseEntity.ok("Account deleted");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Delete failed");
        }
    }
}
