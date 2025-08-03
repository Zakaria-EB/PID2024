package com.reservations.reservations.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepresentationDetailsDTO {
    private Long id;
    private String ShowTitle;
    private String ShowDescription;
    private List<String> showAuthors;
    private List<String> showArtists;
    private List<String> scenographers;

    private LocalDateTime when;
    private String date;
    private String time;

    private String locationName;
    private String locationAddress;
    private String locationCommune;
    private String locationPhone;
    private String locationWebsite;

    //private List<TarifDTO> tarifs;
    private int effectiveCapacity;
    private int bookedSeats;
    private int availableSeats;

}
