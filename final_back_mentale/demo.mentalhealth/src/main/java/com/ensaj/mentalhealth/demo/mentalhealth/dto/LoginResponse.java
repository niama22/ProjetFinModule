package com.ensaj.mentalhealth.demo.mentalhealth.dto;

public class LoginResponse {
    private String email;
    private String message;
    private Long id;  // Add this field

    public LoginResponse() {
    }

    public LoginResponse(String email, String message, Long id) {  // Update constructor
        this.email = email;
        this.message = message;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Add getter and setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}