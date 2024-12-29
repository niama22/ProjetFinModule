package com.ensaj.mentalhealth.demo.mentalhealth.dto;

import lombok.Data;

@Data
public class StatisticDTO {
    private String sentiment;
    private Integer count;
    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}