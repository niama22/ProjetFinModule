package com.pacman.MentAlly.ui.habit;

import com.google.gson.annotations.SerializedName;
import java.util.Calendar;
import java.util.Observable;

public class Habit extends Observable {
    @SerializedName("id")
    private Long habitId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("habitName")
    private String habitName;

    @SerializedName("frequency")
    private String frequency;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("progress")
    private int progress;

    @SerializedName("maxProgress")
    private int maxProgress;

    public Habit() {
    }

    public Habit(Long id, String name, String startDate, String endDate, String freq, int progress) {
        this.habitId = id;
        this.habitName = name;
        this.frequency = freq;
        this.startDate = startDate;
        this.endDate = endDate;
        this.progress = progress;
    }

    // Getters et Setters
    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void incrementProgress() {
        this.progress += 1;
        setChanged();
        notifyObservers(this);
    }
}