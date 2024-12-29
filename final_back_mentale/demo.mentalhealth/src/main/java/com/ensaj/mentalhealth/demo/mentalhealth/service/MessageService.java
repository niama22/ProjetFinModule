package com.ensaj.mentalhealth.demo.mentalhealth.service;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Message;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public Message saveMessage(Message message) {
        try {
            // Set timestamp if not present
            if (message.getTimestamp() == null) {
                message.setTimestamp(LocalDateTime.now());
            }

            logger.info("Saving message: {}", message);
            Message savedMessage = messageRepository.save(message);
            logger.info("Successfully saved message with ID: {}", savedMessage.getId());

            return savedMessage;
        } catch (Exception e) {
            logger.error("Error saving message", e);
            throw e;
        }
    }

    public List<Message> getMessages(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            logger.info("Fetching messages between {} and {}", startTime, endTime);
            return messageRepository.findByTimestampBetween(startTime, endTime);
        } catch (Exception e) {
            logger.error("Error fetching messages by time range", e);
            throw e;
        }
    }

    public List<Message> getMessagesByUserId(Long userId) {
        try {
            logger.info("Fetching messages for user ID: {}", userId);
            return messageRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching messages for user ID: " + userId, e);
            throw e;
        }
    }

    public List<Message> getMessagesByDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

            logger.info("Fetching messages for date {} (from {} to {})",
                    date, startOfDay, endOfDay);

            return messageRepository.findByTimestampBetween(startOfDay, endOfDay);
        } catch (Exception e) {
            logger.error("Error fetching messages for date: " + date, e);
            throw e;
        }
    }

    public List<Message> getMessagesByDateAndUserId(String date, Long userId) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

            logger.info("Fetching messages for date {} and user ID {} (from {} to {})",
                    date, userId, startOfDay, endOfDay);

            return messageRepository.findByTimestampBetweenAndUserId(
                    startOfDay,
                    endOfDay,
                    userId
            );
        } catch (Exception e) {
            logger.error("Error fetching messages for date: " + date + " and user ID: " + userId, e);
            throw e;
        }
    }

    public List<Message> getAllMessages() {
        try {
            logger.info("Fetching all messages");
            return messageRepository.findAllByOrderByTimestampDesc();
        } catch (Exception e) {
            logger.error("Error fetching all messages", e);
            throw e;
        }
    }
}