package com.example.jobportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobportal.entities.Job;
import com.example.jobportal.repositories.JobRepository;

@RestController
@RequestMapping("/admin/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminJobController {

    private final JobRepository jobRepository;

    public AdminJobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Get All Jobs
    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Add Job
    @PostMapping
    public Job addJob(@RequestBody Job job) {
        return jobRepository.save(job);
    }

    // Delete Job
    @DeleteMapping("/{id}")
    public void deleteJob(@PathVariable Long id) {
        jobRepository.deleteById(id);
    }

    // Update Job
    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Job updatedJob) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setTitle(updatedJob.getTitle());
        job.setLocation(updatedJob.getLocation());
       
        job.setDescription(updatedJob.getDescription());
        return jobRepository.save(job);
    }
}