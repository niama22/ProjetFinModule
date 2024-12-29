package com.ensaj.mentalhealth.demo.mentalhealth.repository;


import com.ensaj.mentalhealth.demo.mentalhealth.entity.Feeling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeelingRepository extends JpaRepository<Feeling, Long> {
    List<Feeling> findByUserId(Long userId);
}