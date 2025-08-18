package com.reservations.reservations.mapper;


import com.reservations.reservations.dto.RepresentationDTO;
import com.reservations.reservations.model.Representation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RepresentationMapper {
    public RepresentationDTO toDTO(Representation representation) {
        return new RepresentationDTO(
                representation.getId(),
                representation.getShow().getTitle(),
                representation.getScheduledAt(),
                representation.getLocation() != null ? representation.getLocation().getDesignation() : null,
                representation.getAvailableSeats()
        );
    }

    public List<RepresentationDTO> toDTOList(List<Representation> representations) {
        return representations.stream()
                .map(this::toDTO)
                .collect( Collectors.toList() );
    }

}


