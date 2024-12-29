package com.ensaj.mentalhealth.demo.mentalhealth.repository;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    List<Statistic> findByUserId(Long userId);
    Optional<Statistic> findByUserIdAndSentiment(Long userId, String sentiment);
    void deleteByUserId(Long userId);

}