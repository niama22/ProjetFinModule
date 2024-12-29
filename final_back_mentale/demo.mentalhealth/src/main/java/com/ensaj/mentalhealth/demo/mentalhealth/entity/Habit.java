package com.ensaj.mentalhealth.demo.mentalhealth.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "habits")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String habitName;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false)
    private int progress;

    @Column(nullable = false)
    private int maxProgress;

    // Constructeurs
    public Habit() {}

    public Habit(Long userId, String habitName, LocalDate startDate,
                       LocalDate endDate, String frequency) {
        this.userId = userId;
        this.habitName = habitName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.progress = 0;
        this.calculateMaxProgress();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.calculateMaxProgress();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.calculateMaxProgress();
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
        this.calculateMaxProgress();
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

    // Méthode pour calculer le maxProgress
    private void calculateMaxProgress() {
        if (startDate != null && endDate != null && frequency != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);

            switch (frequency.toLowerCase()) {
                case "daily":
                    this.maxProgress = (int) daysBetween;
                    break;
                case "weekly":
                    this.maxProgress = (int) (daysBetween / 7);
                    break;
                case "monthly":
                    this.maxProgress = (int) (daysBetween / 28);
                    break;
                default:
                    this.maxProgress = (int) daysBetween;
            }
        }
    }
}