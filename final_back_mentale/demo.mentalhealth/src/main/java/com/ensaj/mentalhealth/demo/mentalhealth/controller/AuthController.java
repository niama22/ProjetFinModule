package com.ensaj.mentalhealth.demo.mentalhealth.controller;

import com.ensaj.mentalhealth.demo.mentalhealth.dto.LoginRequest;
import com.ensaj.mentalhealth.demo.mentalhealth.dto.LoginResponse;
import com.ensaj.mentalhealth.demo.mentalhealth.entity.User;
import com.ensaj.mentalhealth.demo.mentalhealth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        try {
            // Use the authenticate method from UserService
            User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

            // Create and return a successful login response
            return new LoginResponse(user.getEmail(), "Login successful", user.getId());
        } catch (RuntimeException e) {
            // Return a failed login response
            return new LoginResponse(null, e.getMessage(), null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Register the user
            User registeredUser = userService.register(user);

            // Return a successful registration response
            return ResponseEntity.ok(new LoginResponse(
                    registeredUser.getEmail(),
                    "Registration successful",
                    registeredUser.getId()
            ));
        } catch (RuntimeException e) {
            // Return an error response if registration fails
            return ResponseEntity.badRequest().body(
                    new LoginResponse(null, e.getMessage(), null)
            );
        }
    }
}