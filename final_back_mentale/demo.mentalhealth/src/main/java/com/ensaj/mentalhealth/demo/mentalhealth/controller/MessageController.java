package com.ensaj.mentalhealth.demo.mentalhealth.controller;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Message;
import com.ensaj.mentalhealth.demo.mentalhealth.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        try {
            logger.info("Received message data: {}", message);

            // Validation détaillée
            Map<String, String> errors = new HashMap<>();

            if (message.getUserId() == null) {
                errors.put("userId", "UserId is required");
            }
            if (message.getText() == null || message.getText().trim().isEmpty()) {
                errors.put("text", "Text is required");
            }

            // Si il y a des erreurs, retourner les détails
            if (!errors.isEmpty()) {
                logger.error("Validation errors: {}", errors);
                return ResponseEntity.badRequest().body(errors);
            }

            // Ensure timestamp is set
            if (message.getTimestamp() == null) {
                message.setTimestamp(LocalDateTime.now());
            }

            // Log des données avant sauvegarde
            logger.info("Saving message: userId={}, text={}, isUser={}, timestamp={}",
                    message.getUserId(), message.getText(), message.isUser(), message.getTimestamp());

            // Save the message
            Message savedMessage = messageService.saveMessage(message);
            logger.info("Successfully saved message with ID: {}", savedMessage.getId());

            return ResponseEntity.ok(savedMessage);

        } catch (Exception e) {
            logger.error("Error saving message: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error saving message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/range")
    public ResponseEntity<?> getMessages(@RequestParam String start, @RequestParam String end) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);
            List<Message> messages = messageService.getMessages(startTime, endTime);
            return ResponseEntity.ok(messages);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date range", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid date format: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("Error retrieving messages", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error retrieving messages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMessages() {
        try {
            List<Message> messages = messageService.getAllMessages();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            logger.error("Error retrieving all messages", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error retrieving messages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMessagesByUserId(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getMessagesByUserId(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            logger.error("Error retrieving messages for user: " + userId, e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error retrieving messages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> getMessagesByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            logger.info("Fetching messages for date: {}", parsedDate);
            List<Message> messages = messageService.getMessagesByDate(date);
            return ResponseEntity.ok(messages);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date: " + date, e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid date format: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("Error retrieving messages", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error retrieving messages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<?> getUserStatistics(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getMessagesByUserId(userId);
            logger.info("DEBUG - Nombre total de messages trouvés: {}", messages.size());

            Map<String, Integer> sentimentStats = new HashMap<>();

            // Initialisation des compteurs
            String[] sentiments = {"sad", "happy", "depressed", "good", "bad", "angry", "excited", "bored", "anxious"};
            for (String sentiment : sentiments) {
                sentimentStats.put(sentiment, 0);
            }

            // Analyse de chaque message (sans vérifier isUser)
            for (Message message : messages) {
                String text = message.getText();
                if (text != null) {
                    text = text.toLowerCase();
                    logger.info("DEBUG - Analyse du message: {}", text);

                    for (String sentiment : sentiments) {
                        if (text.contains(sentiment)) {
                            int currentCount = sentimentStats.get(sentiment);
                            sentimentStats.put(sentiment, currentCount + 1);
                            logger.info("DEBUG - Sentiment '{}' trouvé dans le message: {}", sentiment, text);
                        }
                    }
                }
            }

            logger.info("DEBUG - Statistiques finales: {}", sentimentStats);
            return ResponseEntity.ok(sentimentStats);

        } catch (Exception e) {
            logger.error("ERREUR CRITIQUE - Détails de l'erreur: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur d'analyse: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}