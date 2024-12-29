package com.ensaj.mentalhealth.demo.mentalhealth.controller;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Habit;
import com.ensaj.mentalhealth.demo.mentalhealth.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "*")
public class HabitController {

    @Autowired
    private HabitService habitService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getUserHabits(@PathVariable Long userId) {
        return ResponseEntity.ok(habitService.getUserHabits(userId));
    }

    @GetMapping("/user/{userId}/habit/{habitId}")
    public ResponseEntity<Habit> getHabit(
            @PathVariable Long userId,
            @PathVariable Long habitId) {
        return habitService.getHabit(userId, habitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Habit> createHabit(@RequestBody Habit habit) {
        try {
            Habit createdHabit = habitService.createHabit(habit);
            return ResponseEntity.ok(createdHabit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/user/{userId}/habit/{habitId}")
    public ResponseEntity<Habit> updateHabit(
            @PathVariable Long userId,
            @PathVariable Long habitId,
            @RequestBody Habit habit) {
        try {
            return ResponseEntity.ok(habitService.updateHabit(userId, habitId, habit));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/user/{userId}/habit/{habitId}/progress")
    public ResponseEntity<Habit> updateProgress(
            @PathVariable Long userId,
            @PathVariable Long habitId,
            @RequestParam int progress) {
        try {
            return ResponseEntity.ok(habitService.updateHabitProgress(userId, habitId, progress));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{userId}/habit/{habitId}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long userId,
            @PathVariable Long habitId) {
        habitService.deleteHabit(userId, habitId);
        return ResponseEntity.ok().build();
    }
}