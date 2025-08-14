package com.reservations.reservations.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(length = 60)
    private String status;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepresentationReservation> items = new ArrayList<>();

    public void addItem(RepresentationReservation item) {
        items.add(item);
        item.setReservation(this);
    }

    /**
     * Montant total de la réservation : somme de price × quantity
     */
    @Transient
    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPrice().getPrice() * item.getQuantity())
                .sum();
    }
}