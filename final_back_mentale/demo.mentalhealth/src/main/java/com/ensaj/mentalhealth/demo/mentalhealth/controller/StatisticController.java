package com.ensaj.mentalhealth.demo.mentalhealth.controller;

import com.ensaj.mentalhealth.demo.mentalhealth.dto.StatisticDTO;
import com.ensaj.mentalhealth.demo.mentalhealth.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getStatistics(@PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body("User ID invalide");
            }

            List<StatisticDTO> statistics = statisticService.getStatisticsByUserId(userId);

            if (statistics.isEmpty()) {
                return ResponseEntity.ok()
                        .body("Aucune statistique trouvée pour cet utilisateur");
            }

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des statistiques: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> saveStatistics(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> statistics) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body("User ID invalide");
            }

            if (statistics == null || statistics.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Les statistiques ne peuvent pas être vides");
            }

            statisticService.saveStatistics(userId, statistics);
            return ResponseEntity.ok()
                    .body("Statistiques enregistrées avec succès");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement des statistiques: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/sentiment/{sentiment}")
    public ResponseEntity<?> getStatisticBySentiment(
            @PathVariable Long userId,
            @PathVariable String sentiment) {
        try {
            StatisticDTO statistic = statisticService.getStatisticByUserIdAndSentiment(userId, sentiment);

            if (statistic == null) {
                return ResponseEntity.ok()
                        .body("Aucune statistique trouvée pour ce sentiment");
            }

            return ResponseEntity.ok(statistic);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération de la statistique: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteStatistics(@PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body("User ID invalide");
            }

            statisticService.deleteStatisticsByUserId(userId);
            return ResponseEntity.ok()
                    .body("Statistiques supprimées avec succès");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression des statistiques: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/reset")
    public ResponseEntity<?> resetStatistics(@PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body("User ID invalide");
            }

            statisticService.resetStatistics(userId);
            return ResponseEntity.ok()
                    .body("Statistiques réinitialisées avec succès");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la réinitialisation des statistiques: " + e.getMessage());
        }
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<?> getTotalSentimentsCount(@PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body("User ID invalide");
            }

            Integer totalCount = statisticService.getTotalSentimentsCount(userId);
            return ResponseEntity.ok()
                    .body(Map.of("totalCount", totalCount));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du comptage des sentiments: " + e.getMessage());
        }
    }
}