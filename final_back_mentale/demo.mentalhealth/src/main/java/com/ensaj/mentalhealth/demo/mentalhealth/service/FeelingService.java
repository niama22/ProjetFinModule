package com.ensaj.mentalhealth.demo.mentalhealth.service;
import com.ensaj.mentalhealth.demo.mentalhealth.entity.Feeling;

import com.ensaj.mentalhealth.demo.mentalhealth.repository.FeelingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeelingService {

    @Autowired
    private FeelingRepository feelingRepository;

    public Feeling saveFeeling(Feeling feeling) {
        try {
            return feelingRepository.save(feeling);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("Conflict occurred while saving the Feeling entity: " + e.getMessage());
        }
    }

    public List<Feeling> getFeelingsByUserId(Long userId) {
        return feelingRepository.findByUserId(userId);
    }
}