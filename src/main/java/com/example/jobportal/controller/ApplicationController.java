package com.example.jobportal.controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobportal.entities.Application;
import com.example.jobportal.entities.ApplicationStatus;
import com.example.jobportal.entities.Job;
import com.example.jobportal.entities.User;
import com.example.jobportal.repositories.ApplicationRepository;
import com.example.jobportal.repositories.JobRepository;
import com.example.jobportal.repositories.UserRepository;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    public ApplicationController(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    // =====================================
    // APPLY JOB WITH OPTIONAL RESUME
    // =====================================
    @PostMapping("/apply")
    public ResponseEntity<?> applyJob(
            @RequestParam Long userId,
            @RequestParam Long jobId,
            @RequestParam(required = false) MultipartFile resume
    ) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByUserIdAndJobId(userId, jobId)) {
            return ResponseEntity.badRequest().body("Already applied");
        }

        Application app = new Application();
        app.setUser(user);
        app.setJob(job);
        app.setStatus(ApplicationStatus.PENDING);

        // Resume handling
        if (resume != null && !resume.isEmpty()) {

            if (!"application/pdf".equalsIgnoreCase(resume.getContentType())) {
                return ResponseEntity.badRequest().body("Only PDF allowed");
            }

            if (resume.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Max file size 5MB");
            }

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = System.currentTimeMillis() + "_" +
                    resume.getOriginalFilename().replaceAll("\\s+", "_");

            Path filePath = uploadDir.resolve(fileName).normalize();

            Files.copy(resume.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            app.setResumePath(fileName);
        }

        return ResponseEntity.ok(applicationRepository.save(app));
    }

    // =====================================
    // VIEW RESUME (OPEN PDF IN BROWSER)
    // =====================================
    @GetMapping("/resume/{id}")
    public ResponseEntity<Resource> viewResume(@PathVariable Long id) throws IOException {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getResumePath() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = uploadDir.resolve(app.getResumePath()).normalize();

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);
    }

    // =====================================
    // GET USER APPLICATIONS
    // =====================================
    @GetMapping("/user/{userId}")
    public List<Application> getUserApplications(@PathVariable Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    // =====================================
    // GET ALL APPLICATIONS (ADMIN)
    // =====================================
    @GetMapping("/all")
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // =====================================
    // UPDATE STATUS (ADMIN)
    // =====================================
    @PutMapping("/update-status/{id}")
    public Application updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus(status);
        return applicationRepository.save(app);
    }

    // =====================================
    // CANCEL APPLICATION (USER)
    // =====================================
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<?> cancelApplication(@PathVariable Long id) {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            return ResponseEntity.badRequest().body("Cannot cancel after review");
        }

        applicationRepository.delete(app);
        return ResponseEntity.ok("Application cancelled");
    }
}