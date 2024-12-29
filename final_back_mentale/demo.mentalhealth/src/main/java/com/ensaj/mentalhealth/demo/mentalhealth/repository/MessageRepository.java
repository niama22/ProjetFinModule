package com.ensaj.mentalhealth.demo.mentalhealth.repository;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    // Méthode utilisant la convention de nommage JPA
    List<Message> findAllByOrderByTimestampDesc();

    // Alternative avec une requête JPQL personnalisée
    @Query("SELECT m FROM Message m ORDER BY m.timestamp DESC")
    List<Message> getAllMessagesOrderedByDate();


    // Version avec pagination si nécessaire
    @Query(value = "SELECT * FROM messages ORDER BY timestamp DESC LIMIT 100", nativeQuery = true)
    List<Message> getLast100Messages();
    List<Message> findByUserId(Long userId);// Implement this method
    List<Message> findByTimestampBetweenAndUserId(LocalDateTime start, LocalDateTime end, Long userId);


}

