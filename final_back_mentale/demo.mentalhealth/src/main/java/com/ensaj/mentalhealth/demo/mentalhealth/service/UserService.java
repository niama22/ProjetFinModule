package com.ensaj.mentalhealth.demo.mentalhealth.service;
import com.ensaj.mentalhealth.demo.mentalhealth.entity.User;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Authentification sans hachage du mot de passe
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Comparaison du mot de passe en clair
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    // Enregistrement sans hachage du mot de passe
    public User register(User user) {
        // Vérification si l'utilisateur existe déjà (par exemple avec l'email)
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        // Enregistrer l'utilisateur dans la base de données
        return userRepository.save(user);

    }

        // Nouvelle méthode pour récupérer un utilisateur par email
        public User getUserByEmail(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

    public User updateUser(Long id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Mettre à jour les champs de l'utilisateur
            if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
            if (userDetails.getFirstName() != null) user.setFirstName(userDetails.getFirstName());
            if (userDetails.getLastName() != null) user.setLastName(userDetails.getLastName());
            if (userDetails.getDob() != null) user.setDob(userDetails.getDob());
            if (userDetails.getGender() != null) user.setGender(userDetails.getGender());
            if (userDetails.getCountry() != null) user.setCountry(userDetails.getCountry());

            // Mettre à jour le mot de passe seulement si le nouveau mot de passe est non nul
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(userDetails.getPassword()); // Assurez-vous que ce champ est bien pris en compte
            }

            return userRepository.save(user);  // Sauvegarder l'utilisateur avec les nouveaux détails
        } else {
            return null; // L'utilisateur n'a pas été trouvé
        }
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
