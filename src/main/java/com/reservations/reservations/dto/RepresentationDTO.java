package com.reservations.reservations.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Getter
@Setter

public class RepresentationDTO {

    private Long id;
    private String showTitle;
    private LocalDateTime when;
    private String locationDesignation;
    private int availableSeats;


    public RepresentationDTO(Long id, String showTitle, LocalDateTime when, String locationDesignation, int availableSeats) {
        this.id = id;
        this.showTitle = showTitle;
        this.when = when;
        this.locationDesignation = locationDesignation;
        this.availableSeats = availableSeats;
    }




}