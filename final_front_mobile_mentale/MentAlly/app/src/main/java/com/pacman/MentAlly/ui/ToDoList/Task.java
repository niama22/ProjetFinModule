package com.pacman.MentAlly.ui.ToDoList;
public class Task {
    private Long id;
    private String name; //mandatory
    private String startDate; //optional
    private String endDate; //optional
    private static int taskIdCounter = 0;  // Initialise le compteur à 0
    private boolean completed;
    private String taskId;

    // Constructor with all parameters
    public Task(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.completed = false;
        this.taskId = generateTaskId();
    }

    // Default constructor
    public Task() {
        this.completed = false;
        this.taskId = generateTaskId();
    }

    // Generate a unique task ID for each task
    private String generateTaskId() {
        taskIdCounter++;
        return Integer.toString(taskIdCounter);
    }

    // Getter and setter for taskId
    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    // Getter and setter for other fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Additional methods
    public void markAsCompleted() {
        this.completed = true;
    }

    public void markAsPending() {
        this.completed = false;
    }

    // Display methods (if needed for UI)
    public String getTaskName() {
        return this.name;
    }

    public String getTaskStatus() {
        return this.completed ? "Completed" : "Pending";
    }
}
