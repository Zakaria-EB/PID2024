package com.reservations.reservations.mapper;


import com.reservations.reservations.dto.ArtistDTO;
import com.reservations.reservations.dto.ArtistTypeDTO;
import com.reservations.reservations.model.Artist;
import com.reservations.reservations.model.Type;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtistMapper {
    public ArtistDTO toDTO(Artist artist) {
        ArtistDTO dto = new ArtistDTO();
        dto.setId(artist.getId());
        dto.setFirstname(artist.getFirstname());
        dto.setLastname(artist.getLastname());
        return dto;
    }

    public Artist toEntity(ArtistDTO dto) {
        Artist artist = new Artist();
        artist.setId(dto.getId());
        artist.setFirstname(dto.getFirstname());
        artist.setLastname(dto.getLastname());
        return artist;
    }

    public ArtistTypeDTO toArtistTypeDTO(Artist artist) {
        if (artist == null) return null;

        List<String> types = artist.getTypes()
                .stream()
                .map( Type::getType )
                .collect(Collectors.toList());

        return new ArtistTypeDTO(
                artist.getId(),
                artist.getFirstname(),
                artist.getLastname(),
                types
        );
    }

}
