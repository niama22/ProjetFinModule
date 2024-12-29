package com.pacman.MentAlly.ui.FaceTracker;

import android.os.Build;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

public class Feeling {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("emotion")
    private String emotion;

    @SerializedName("timestamp")
    private String timestamp;

    public Feeling(Long userId, String emotion) {
        this.userId = userId;
        this.emotion = emotion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.timestamp = LocalDateTime.now().toString();
        }
    }

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}