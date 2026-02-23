package com.example.jobportal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("applications")
    private User user;


    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonIgnoreProperties("applications")
    private Job job;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    
    private String resumePath;

    public Application() {
    }

    public Application(Long id, User user, Job job, ApplicationStatus status, String resumePath) {
        this.id = id;
        this.user = user;
        this.job = job;
        this.status = status;
        this.resumePath =resumePath;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public String getResumePath() {return resumePath;}
    public void  setResumePath(String resumepath) { this.resumePath =resumepath;}
    
    
}