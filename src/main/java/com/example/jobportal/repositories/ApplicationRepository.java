package com.example.jobportal.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobportal.entities.Application;
import com.example.jobportal.entities.ApplicationStatus;
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long userId);
    long countByStatus(ApplicationStatus status);
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
}