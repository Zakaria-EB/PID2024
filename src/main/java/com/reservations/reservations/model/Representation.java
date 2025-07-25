package com.reservations.reservations.model;



import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "representations")
public class Representation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    /**
     * Date de création de la représentation
     */
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    /**
     * Lieu de prestation de la représentation
     */
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;

    /**
     * Jauge mise en vente (peut être null → on tombera sur location.capacity)
     */
    @Column(nullable = false)
    private Integer capacity;

    /**
     * Réservations de cette représentation, avec quantité et prix
     */
    @OneToMany(mappedBy = "representation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RepresentationReservation> items = new ArrayList<>();

    public Representation() {
    }

    public Representation(Show show, LocalDateTime scheduledAt, Location location, Integer capacity, List<RepresentationReservation> items) {
        this.show = show;
        this.scheduledAt = scheduledAt;
        this.location = location;
        this.capacity = capacity;
        this.items = items;
    }

    public void setItems(List<RepresentationReservation> items) {
        this.items = items;
    }


    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Retourne la liste des utilisateurs ayant réservé
     */
    @Transient
    public List<User> getUsers() {
        return items.stream()
                .map(RepresentationReservation::getReservation)
                .map(Reservation::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Capacité effective : soit la capacité propre, soit celle du lieu
     */
    @Transient
    public int getEffectiveCapacity() {
        if (capacity != null) return capacity;
        return (location != null ? location.getCapacity() : 0);
    }

    /**
     * Somme des quantités déjà réservées
     */
    @Transient
    public int getBookedSeats() {
        return items.stream()
                .mapToInt(RepresentationReservation::getQuantity)
                .sum();
    }

    /**
     * Places restantes
     */
    @Transient
    public int getAvailableSeats() {
        return getEffectiveCapacity() - getBookedSeats();
    }

    @Override
    public String toString() {
        return "Representation{" +
                "id=" + id +
                ", show=" + show +
                ", scheduledAt=" + scheduledAt +
                ", location=" + location +
                '}';
    }

}