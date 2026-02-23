package com.example.jobportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobportal.entities.Application;
import com.example.jobportal.repositories.ApplicationRepository;

@RestController
@RequestMapping("/admin/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminApplicationController {

    private final ApplicationRepository applicationRepository;

    public AdminApplicationController(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @GetMapping
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }
}