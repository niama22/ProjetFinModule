package com.ensaj.mentalhealth.demo.mentalhealth.controller;


import com.ensaj.mentalhealth.demo.mentalhealth.entity.Feeling;
import com.ensaj.mentalhealth.demo.mentalhealth.service.FeelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feelings")
public class FeelingController {

    @Autowired
    private FeelingService feelingService;

    @PostMapping
    public ResponseEntity<Feeling> createFeeling(@RequestBody Feeling feeling) {
        Feeling savedFeeling = feelingService.saveFeeling(feeling);
        return new ResponseEntity<>(savedFeeling, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feeling>> getFeelingsByUserId(@PathVariable Long userId) {
        List<Feeling> feelings = feelingService.getFeelingsByUserId(userId);
        return new ResponseEntity<>(feelings, HttpStatus.OK);
    }
}