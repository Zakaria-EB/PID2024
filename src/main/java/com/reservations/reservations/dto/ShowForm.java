package com.reservations.reservations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShowForm {
    @NotBlank
    private String title;
    private String description;
    private String posterUrl;
    @NotNull
    private boolean bookable;
    private Long locationId;
    @NotNull(message = "Le tarif plein est obligatoire")
    private Double fullPrice;
    private Double reducedPrice;
    private List<RepForm> representations = new ArrayList<>();
    private List<CollabForm> collaborators = new ArrayList<>();
    private List<Long> tagIds = new ArrayList<>();
    private List<String> newTags = new ArrayList<>();
    // Tarif plein
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fullPriceStartDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fullPriceEndDate;


    // Tarif réduit
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reducedPriceStartDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reducedPriceEndDate;

}