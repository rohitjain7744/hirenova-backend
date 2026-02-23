package com.example.jobportal.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobportal.entities.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Custom query methods (if needed)
    List<Job> findByLocation(String location);

    List<Job> findByCompany(String company);
    Page<Job> findByTitleContainingIgnoreCase(String keyword, PageRequest pageRequest);

}