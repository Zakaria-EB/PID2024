package com.reservations.reservations.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class PriceDTO {
    private Long id;
    private String type;
    private Double price;
    private LocalDate startDate;
    private LocalDate endDate;

    public PriceDTO(Long id, String type, Double price, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
