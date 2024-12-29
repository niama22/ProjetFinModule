package com.ensaj.mentalhealth.demo.mentalhealth.repository;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
    List<Mood> findByUserId(Long userId);
}