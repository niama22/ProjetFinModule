package com.pacman.MentAlly.ui.home;

import com.pacman.MentAlly.ui.chatbot.Message;

import java.util.List;

public class ConversationGroup {
    private String date;
    private List<Message> messages;
    private String previewText;

    public ConversationGroup(String date, List<Message> messages) {
        this.date = date;
        this.messages = messages;
        this.previewText = generatePreviewText();
    }

    private String generatePreviewText() {
        if (!messages.isEmpty()) {
            StringBuilder preview = new StringBuilder();
            int count = Math.min(2, messages.size());
            for (int i = 0; i < count; i++) {
                preview.append(messages.get(i).getText()).append(" ");
            }
            return preview.toString().trim();
        }
        return "";
    }

    public String getDate() {
        return date;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getPreviewText() {
        return previewText;
    }
}