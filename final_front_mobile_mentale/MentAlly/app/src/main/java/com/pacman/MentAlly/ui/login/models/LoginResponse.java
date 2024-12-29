package com.pacman.MentAlly.ui.login.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("email")
    private String email;

    @SerializedName("message")
    private String message;

    @SerializedName("id")  // Correspond au nom du champ dans la réponse JSON
    private Long id;

    public LoginResponse(String email, String message, Long id) {
        this.email = email;
        this.message = message;
        this.id = id;
    }

    public LoginResponse() {}

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(Long id) {
        this.id = id;
    }
}