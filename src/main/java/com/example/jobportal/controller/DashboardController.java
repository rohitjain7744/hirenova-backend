package com.example.jobportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.example.jobportal.entities.ApplicationStatus;
import com.example.jobportal.repositories.ApplicationRepository;
import com.example.jobportal.repositories.JobRepository;
import com.example.jobportal.repositories.UserRepository;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public DashboardController(
            UserRepository userRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }
    
   
    @GetMapping("/counts")
    public Map<String, Long> getCounts() {

        Map<String, Long> counts = new HashMap<>();

        counts.put("users", userRepository.count());
        counts.put("jobs", jobRepository.count());
        counts.put("applications", applicationRepository.count());
        counts.put("pending", applicationRepository.countByStatus(ApplicationStatus.PENDING));
        return counts;
    }
}