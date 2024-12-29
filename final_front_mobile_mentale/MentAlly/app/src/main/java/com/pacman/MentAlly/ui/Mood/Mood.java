package com.pacman.MentAlly.ui.Mood;

import java.io.Serializable;

public class Mood implements Serializable {
    private Long id;
    private Long userId;
    private String date;
    private String description;
    private String moodType;

    public Mood() {}

    public Mood(Long userId, String date, String description, String moodType) {
        this.userId = userId;
        this.date = date;
        this.description = description;
        this.moodType = moodType;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMoodType() { return moodType; }
    public void setMoodType(String moodType) { this.moodType = moodType; }

    @Override
    public String toString() {
        return "Mood{" +
                "id=" + id +
                ", userId=" + userId +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", moodType='" + moodType + '\'' +
                '}';
    }
}