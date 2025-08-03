package com.reservations.reservations.controller;


import com.reservations.reservations.model.Reservation;
import com.reservations.reservations.model.User;
import com.reservations.reservations.repository.UserRepository;
import com.reservations.reservations.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public Reservation getReservation(@PathVariable Long id) {
        return reservationService.getReservation(id);
    }

    @PostMapping
    public Reservation addReservation(@RequestBody Reservation r) {
        return reservationService.addReservation(r);
    }

    @PutMapping("/{id}")
    public Reservation updateReservation(@PathVariable Long id,
                                         @RequestBody Reservation r) {
        return reservationService.updateReservation(id, r);
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getReservationsByUser(@PathVariable Long userId) {
        return reservationService.findByUserId(userId);
    }

    @GetMapping("/me")
    public List<Reservation> getMyReservations(Principal principal) {
        User user = userRepository.findByLogin(principal.getName());
        if (user == null) {
            return Collections.emptyList();
        }
        return reservationService.findByUserId(user.getId());
    }
}
