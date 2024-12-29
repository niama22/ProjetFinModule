package com.ensaj.mentalhealth.demo.mentalhealth.service;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Habit;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    public List<Habit> getUserHabits(Long userId) {
        return habitRepository.findByUserIdOrderByStartDateDesc(userId);
    }

    public Habit createHabit(Habit habit) {
        // Validation de base
        if (habit.getUserId() == null || habit.getHabitName() == null) {
            throw new IllegalArgumentException("UserId and HabitName are required");
        }
        return habitRepository.save(habit);
    }

    public Optional<Habit> getHabit(Long userId, Long habitId) {
        Optional<Habit> habit = habitRepository.findById(habitId);
        if (habit.isPresent() && !habit.get().getUserId().equals(userId)) {
            return Optional.empty();
        }
        return habit;
    }

    public Habit updateHabitProgress(Long userId, Long habitId, int progress) {
        Optional<Habit> habitOpt = getHabit(userId, habitId);
        if (habitOpt.isEmpty()) {
            throw new RuntimeException("Habit not found or unauthorized");
        }

        Habit habit = habitOpt.get();
        if (progress >= 0 && progress <= habit.getMaxProgress()) {
            habit.setProgress(progress);
            return habitRepository.save(habit);
        } else {
            throw new IllegalArgumentException("Invalid progress value");
        }
    }

    public void deleteHabit(Long userId, Long habitId) {
        habitRepository.deleteByUserIdAndId(userId, habitId);
    }

    public Habit updateHabit(Long userId, Long habitId, Habit updatedHabit) {
        Optional<Habit> habitOpt = getHabit(userId, habitId);
        if (habitOpt.isEmpty()) {
            throw new RuntimeException("Habit not found or unauthorized");
        }

        Habit habit = habitOpt.get();
        habit.setHabitName(updatedHabit.getHabitName());
        habit.setEndDate(updatedHabit.getEndDate());
        habit.setFrequency(updatedHabit.getFrequency());

        return habitRepository.save(habit);
    }
}