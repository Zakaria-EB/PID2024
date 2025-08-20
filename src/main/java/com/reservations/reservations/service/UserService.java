package com.reservations.reservations.service;

import com.reservations.reservations.model.User;
import com.reservations.reservations.repository.UserRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
    public User findByLogin(String login) {
        return userRepository.findByLogin(login); // sans Optional
    }

    public User getUser(long id) {
        return userRepository.findById(id);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(long id, User user) {
        userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Autowired
    private UserRepository repository;


    public User getUser(String id) {
        int indice = Integer.parseInt(id);

        return repository.findById(indice);
    }

    public void updateUser(String id, User user) {
        repository.save(user);
    }

    public void deleteUser(String id) {
        Long indice = (long) Integer.parseInt(id);

        repository.deleteById(indice);
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }
    public User save(User user) {
        return userRepository.save(user);
    }
}
