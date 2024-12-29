package com.ensaj.mentalhealth.demo.mentalhealth.service;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Mood;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
public class MoodService {
    @Autowired
    private MoodRepository moodRepository;

    public Mood createMood(Mood mood) {
        return moodRepository.save(mood);
    }

    public List<Mood> getMoodsByUserId(Long userId) {
        return moodRepository.findByUserId(userId);
    }

    public Optional<Mood> getMoodById(Long id) {
        return moodRepository.findById(id);
    }

    public Mood updateMood(Long id, Mood moodDetails) {
        Mood mood = moodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mood not found"));

        mood.setDate(moodDetails.getDate());
        mood.setDescription(moodDetails.getDescription());
        mood.setMoodType(moodDetails.getMoodType());

        return moodRepository.save(mood);
    }

    public void deleteMood(Long id) {
        Mood mood = moodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mood not found"));

        moodRepository.delete(mood);
    }

    public void deleteUserMoods(Long userId) {
        List<Mood> userMoods = moodRepository.findByUserId(userId);
        moodRepository.deleteAll(userMoods);
    }
}