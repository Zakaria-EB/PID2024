package com.reservations.reservations.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private Double price;

    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "price")
    private List<RepresentationReservation> representationReservations = new ArrayList<>();

    @ManyToMany(mappedBy = "prices")
    private List<Show> shows = new ArrayList<>();
}
