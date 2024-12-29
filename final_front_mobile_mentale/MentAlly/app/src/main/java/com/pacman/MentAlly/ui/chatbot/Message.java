package com.pacman.MentAlly.ui.chatbot;

public class Message {
    private Long id;
    private String text;
    private boolean isUser;
    private boolean isDateHeader;
    private String userId;
    private String timestamp;

    public Message(String text, boolean isUser, boolean isDateHeader) {
        this.text = text;
        this.isUser = isUser;
        this.isDateHeader = isDateHeader;
    }

    public Message() {}

    // Add getters and setters for new fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    // Existing getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isUser() { return isUser; }
    public void setUser(boolean user) { isUser = user; }

    public boolean isDateHeader() { return isDateHeader; }
    public void setDateHeader(boolean dateHeader) { isDateHeader = dateHeader; }
}