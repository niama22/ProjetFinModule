package com.ensaj.mentalhealth.demo.mentalhealth.repository;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserId(Long userId);
    List<Habit> findByUserIdOrderByStartDateDesc(Long userId);
    void deleteByUserIdAndId(Long userId, Long id);
}