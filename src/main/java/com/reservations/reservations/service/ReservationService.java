package com.reservations.reservations.service;



import com.reservations.reservations.model.Reservation;
import com.reservations.reservations.model.User;
import com.reservations.reservations.repository.ReservationRepository;
import com.reservations.reservations.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository repository;
    @Autowired
    private UserRepository userRepo;

    public List<Reservation> getAllReservations() {
        return (List<Reservation>) repository.findAll();
    }

    public Reservation getReservation(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Reservation addReservation(Reservation reservation) {
        return repository.save(reservation);
    }

    public Reservation updateReservation(Long id, Reservation reservation) {
        reservation.setId(id);
        return repository.save(reservation);
    }

    public void deleteReservation(Long id) {
        repository.deleteById(id);
    }

    /**
     * Renvoie les réservations associées à un utilisateur donné.
     */
    public List<Reservation> findByUserId(Long userId) {
        Optional<User> u = userRepo.findById(userId);
        return u.map(repository::findByUser)
                .orElse(Collections.emptyList());
    }
    public List<Reservation> findByUserIdAndStatus(Long userId, String status) {
        return repository.findByUserIdAndStatus(userId, status);
    }


    /**
     * Pour créer ou mettre à jour une réservation
     */
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }
}


