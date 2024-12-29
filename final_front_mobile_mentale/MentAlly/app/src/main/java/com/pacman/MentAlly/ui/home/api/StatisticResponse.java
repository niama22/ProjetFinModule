package com.pacman.MentAlly.ui.home.api;

public class StatisticResponse {
    private Long userId;
    private String sentiment;
    private Integer count;
    private String createdAt;
    private String updatedAt;

    // Constructeur par défaut
    public StatisticResponse() {
    }

    // Constructeur avec paramètres
    public StatisticResponse(Long userId, String sentiment, Integer count, String createdAt, String updatedAt) {
        this.userId = userId;
        this.sentiment = sentiment;
        this.count = count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "StatisticResponse{" +
                "userId=" + userId +
                ", sentiment='" + sentiment + '\'' +
                ", count=" + count +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}