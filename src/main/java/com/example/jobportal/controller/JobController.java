 package com.example.jobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobportal.entities.Job;
import com.example.jobportal.repositories.JobRepository;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @PostMapping
    public Job addJob(@RequestBody Job job) {
        return jobRepository.save(job);
    }
    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @GetMapping
    public Page<Job> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return jobRepository.findAll(PageRequest.of(page, size));
    }
    @GetMapping("/search")
    public Page<Job> searchJobs(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size) {

        return jobRepository.findByTitleContainingIgnoreCase(
                keyword,
                PageRequest.of(page, size)
        );
    }
}