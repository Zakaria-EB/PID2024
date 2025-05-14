package com.reservations.reservations.controller;

import com.reservations.reservations.model.User;
import com.reservations.reservations.repository.UserRepository;
import com.reservations.reservations.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userRepository.findByLogin(user.getLogin()) != null) {
            model.addAttribute("error", "Ce login est déjà utilisé. Veuillez en choisir un autre.");
            return "register/register"; // Renvoyer le formulaire d'inscription avec le message d'erreur
        }
        try {
            // Encodage du mot de passe
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // Ajout de la date de création
            user.setCreated_at(LocalDateTime.now());

            // Enregistrement de l'utilisateur
            userRepository.save(user);

            // Redirection vers la page de connexion après inscription
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register/register"; // Retourne au formulaire d'inscription en cas d'erreur
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); // Prépare un nouvel utilisateur pour le formulaire
        return "register/register";
    }
}
