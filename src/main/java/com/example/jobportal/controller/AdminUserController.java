package com.example.jobportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobportal.entities.User;
import com.example.jobportal.repositories.UserRepository;


	
	@RestController
	@RequestMapping("/admin/users")
	@CrossOrigin(origins = "http://localhost:5173")
	public class AdminUserController {

	    private final UserRepository userRepository;

	    public AdminUserController(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    // ✅ GET ALL USERS
	    @GetMapping
	    public List<User> getAllUsers() {
	        return userRepository.findAll();
	    }

}
