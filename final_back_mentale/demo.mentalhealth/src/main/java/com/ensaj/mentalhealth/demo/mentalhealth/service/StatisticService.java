package com.ensaj.mentalhealth.demo.mentalhealth.service;

import com.ensaj.mentalhealth.demo.mentalhealth.dto.StatisticDTO;
import com.ensaj.mentalhealth.demo.mentalhealth.entity.Statistic;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.StatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    public List<StatisticDTO> getStatisticsByUserId(Long userId) {
        return statisticRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveStatistics(Long userId, Map<String, Integer> sentimentStats) {
        sentimentStats.forEach((sentiment, count) -> {
            statisticRepository.findByUserIdAndSentiment(userId, sentiment)
                    .ifPresentOrElse(
                            statistic -> {
                                statistic.setCount(count);
                                statisticRepository.save(statistic);
                            },
                            () -> {
                                Statistic newStatistic = new Statistic();
                                newStatistic.setUserId(userId);
                                newStatistic.setSentiment(sentiment);
                                newStatistic.setCount(count);
                                statisticRepository.save(newStatistic);
                            }
                    );
        });
    }

    public StatisticDTO getStatisticByUserIdAndSentiment(Long userId, String sentiment) {
        return statisticRepository.findByUserIdAndSentiment(userId, sentiment)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public void deleteStatisticsByUserId(Long userId) {
        statisticRepository.deleteByUserId(userId);
    }

    @Transactional
    public void resetStatistics(Long userId) {
        List<Statistic> statistics = statisticRepository.findByUserId(userId);
        statistics.forEach(statistic -> {
            statistic.setCount(0);
            statisticRepository.save(statistic);
        });
    }

    public Integer getTotalSentimentsCount(Long userId) {
        return statisticRepository.findByUserId(userId)
                .stream()
                .mapToInt(Statistic::getCount)
                .sum();
    }

    public void updateStatistic(Long userId, String sentiment, Integer newCount) {
        statisticRepository.findByUserIdAndSentiment(userId, sentiment)
                .ifPresent(statistic -> {
                    statistic.setCount(newCount);
                    statisticRepository.save(statistic);
                });
    }

    private StatisticDTO convertToDTO(Statistic statistic) {
        StatisticDTO dto = new StatisticDTO();
        dto.setSentiment(statistic.getSentiment());
        dto.setCount(statistic.getCount());
        dto.setUserId(statistic.getUserId());
        return dto;
    }
}