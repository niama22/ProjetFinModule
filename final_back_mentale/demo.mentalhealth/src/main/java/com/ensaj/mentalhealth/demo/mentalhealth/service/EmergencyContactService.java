package com.ensaj.mentalhealth.demo.mentalhealth.service;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.EmergencyContact;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.EmergencyContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmergencyContactService {

    @Autowired
    private EmergencyContactRepository repository;

    public List<EmergencyContact> getAllContactsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public EmergencyContact saveContact(EmergencyContact contact) {
        if (contact.getUserId() == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (contact.getPhoneNumber() == null || contact.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("phoneNumber cannot be null or empty");
        }
        if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
        return repository.save(contact);
    }
    @Transactional
    public void deleteContact(Long userId, Long contactId) {
        repository.deleteByUserIdAndId(userId, contactId);
    }

    @Transactional
    public void deleteAllContactsByUserId(Long userId) {
        List<EmergencyContact> contacts = repository.findByUserId(userId);
        repository.deleteAll(contacts);
    }
}