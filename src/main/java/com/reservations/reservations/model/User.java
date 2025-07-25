package com.reservations.reservations.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String langue;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDateTime created_at;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Toutes les réservations passées par cet utilisateur.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Retourne la liste des spectacles (Representation) réservés par cet utilisateur,
     * en parcourant toutes ses Reservation → RepresentationReservation.
     */
    @Transient
    public List<Representation> getRepresentations() {
        return reservations.stream()
                .flatMap(res -> res.getItems().stream())
                .map(item -> item.getRepresentation())
                .distinct()
                .collect(Collectors.toList());
    }

    //pour commentaires
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }


    // Méthode utilitaire pour lier une nouvelle réservation
    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setUser(this);
    }

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }


    @Override
    public String toString() {
        return "login{" +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", role=" + role +
                '}';
    }
}