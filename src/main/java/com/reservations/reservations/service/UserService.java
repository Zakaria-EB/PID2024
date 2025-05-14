package com.reservations.reservations.service;


import com.reservations.reservations.model.Role;
import com.reservations.reservations.model.User;
import com.reservations.reservations.repository.RoleRepository;
import com.reservations.reservations.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Méthode pour sauvegarder un utilisateur
    public void saveUser(User user) {
        // Vérification si le login est déjà pris
        Optional<User> existingUser = Optional.ofNullable(repository.findByLogin(user.getLogin()));
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Ce login est déjà utilisé !");
        }

        // Chiffre le mot de passe avant de le sauvegarder dans la base de données
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Ajout de la date de création
        user.setCreated_at(LocalDateTime.now());

        // Cherche le rôle "member" dans la base
        Role userRole = roleRepository.findByRole("member");

        // Ajoute le rôle à l'utilisateur
        user.addRole(userRole);

        // Sauvegarde de l'utilisateur dans la base de données
        repository.save(user);
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        repository.findAll().forEach(users::add);

        return users;
    }

    public User getUser(String id) {
        int indice = Integer.parseInt(id);

        return repository.findById(indice);
    }

    public void addUser(User user) {
        repository.save(user);
    }

    public void updateUser(String id, User user) {
        repository.save(user);
    }

    public void deleteUser(String id) {
        Long indice = (long) Integer.parseInt(id);

        repository.deleteById(indice);
    }

}
