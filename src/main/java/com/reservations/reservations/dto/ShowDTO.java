package com.reservations.reservations.dto;



import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Getter
public class ShowDTO {

    private Long id;
    private String title;
    private String posterUrl;
    private LocalDateTime createdAt;
    private boolean bookable;

    private String description;
    private String location;
    private Set<String> tags;

    private List<Double> prices;
    private Double minPrice;

    private int representationsCount;
    private List<RepresentationDTO> representations;

    private Map<String, List<ArtistDTO>> collaborateurs;


    public ShowDTO(Long id, String title, String posterUrl, LocalDateTime createdAt, boolean bookable, String description, String location, Set<String> tags, List<Double> prices, Double minPrice, int representationsCount, List<RepresentationDTO> representations, Map<String, List<ArtistDTO>> collaborateurs) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.createdAt = createdAt;
        this.bookable = bookable;
        this.description = description;
        this.location = location;
        this.tags = tags;
        this.prices = prices;
        this.minPrice = minPrice;
        this.representationsCount = representationsCount;
        this.representations = representations;
        this.collaborateurs = collaborateurs;
    }


}
