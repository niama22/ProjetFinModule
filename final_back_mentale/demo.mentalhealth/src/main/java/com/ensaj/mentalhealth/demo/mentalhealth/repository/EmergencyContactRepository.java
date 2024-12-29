package com.ensaj.mentalhealth.demo.mentalhealth.repository;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByUserId(Long userId);
    void deleteByUserIdAndId(Long userId, Long id);
}