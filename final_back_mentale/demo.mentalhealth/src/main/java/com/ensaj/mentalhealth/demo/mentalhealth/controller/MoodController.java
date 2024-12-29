package com.ensaj.mentalhealth.demo.mentalhealth.controller;


import com.ensaj.mentalhealth.demo.mentalhealth.entity.Mood;
import com.ensaj.mentalhealth.demo.mentalhealth.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
@CrossOrigin(origins = "*")
public class MoodController {
    @Autowired
    private MoodService moodService;

    @PostMapping
    public ResponseEntity<Mood> createMood(@RequestBody Mood mood) {
        Mood createdMood = moodService.createMood(mood);
        return ResponseEntity.ok(createdMood);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Mood>> getUserMoods(@PathVariable Long userId) {
        List<Mood> moods = moodService.getMoodsByUserId(userId);
        return ResponseEntity.ok(moods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mood> getMood(@PathVariable Long id) {
        return moodService.getMoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mood> updateMood(@PathVariable Long id, @RequestBody Mood moodDetails) {
        Mood updatedMood = moodService.updateMood(id, moodDetails);
        return ResponseEntity.ok(updatedMood);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMood(@PathVariable Long id) {
        moodService.deleteMood(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUserMoods(@PathVariable Long userId) {
        moodService.deleteUserMoods(userId);
        return ResponseEntity.ok().build();
    }
}