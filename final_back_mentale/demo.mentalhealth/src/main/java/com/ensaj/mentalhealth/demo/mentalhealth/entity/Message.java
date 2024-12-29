package com.ensaj.mentalhealth.demo.mentalhealth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private boolean isUser;
    private LocalDateTime timestamp;
    private Long userId;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isUser() { return isUser; }
    public void setUser(boolean user) { isUser = user; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}