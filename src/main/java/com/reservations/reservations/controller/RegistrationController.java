package com.reservations.reservations.controller;

import com.reservations.reservations.model.User;
import com.reservations.reservations.model.UserRole;
import com.reservations.reservations.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "authentication/registration"; // Le template registration.html
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        // Vérifier que l'user n'existe pas déjà
        if (userService.getAllUsers().stream().anyMatch(u -> u.getLogin().equalsIgnoreCase(user.getLogin()))) {
            model.addAttribute("errorMessage", "Cet utilisateur existe déjà.");
            return "authentication/registration";
        }

        // Encoder le mdp avant la sauvegarde
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Initialiser created_at avec la date et l'heure actuelles
        user.setCreated_at(LocalDateTime.now());
        // Définir rôle par défaut
        user.setRole(UserRole.MEMBER);
        // Vous pouvez également définir d'autres valeurs par défaut (langue, etc.)
        if (user.getLangue() == null || user.getLangue().isEmpty()) {
            user.setLangue("fr"); // ou une autre valeur par défaut
        }
        // Sauvegarder l'user
        userService.addUser(user);

        // Rediriger vers la page de login
        return "redirect:/login?registrationSuccess=true";
    }
}
