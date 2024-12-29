package com.ensaj.mentalhealth.demo.mentalhealth.controller;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.EmergencyContact;
import com.ensaj.mentalhealth.demo.mentalhealth.service.EmergencyContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency-contacts")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactService service;

    @GetMapping("/{userId}")
    public ResponseEntity<List<EmergencyContact>> getAllContacts(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getAllContactsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<EmergencyContact> createContact(@RequestBody EmergencyContact contact) {
        if (contact.getUserId() == null || contact.getName() == null ||
                contact.getPhoneNumber() == null || contact.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.saveContact(contact));
    }
    @DeleteMapping("/{userId}/{contactId}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long userId, @PathVariable Long contactId) {
        service.deleteContact(userId, contactId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteAllContacts(@PathVariable Long userId) {
        service.deleteAllContactsByUserId(userId);
        return ResponseEntity.ok().build();
    }
}