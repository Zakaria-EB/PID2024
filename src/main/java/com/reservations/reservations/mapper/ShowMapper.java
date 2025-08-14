package com.reservations.reservations.mapper;



import com.reservations.reservations.dto.ArtistDTO;
import com.reservations.reservations.dto.RepresentationDTO;
import com.reservations.reservations.dto.ShowDTO;
import com.reservations.reservations.model.ArtistType;
import com.reservations.reservations.model.Price;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;


@Component
public class ShowMapper {

    @Autowired
    private RepresentationMapper representationMapper;

    public ShowDTO mapShowToShowDTO(Show show) {
        if (show == null) {
            return null;
        }

        List<Double> prices = show.getPrices().stream()
                .map(Price::getPrice)
                .collect(Collectors.toList());

        // ne sera pas utilisé car on veut afficher le prix normal et non réduit
        Double minPrice = show.getPrices().isEmpty() ? null :
                show.getPrices().get(0).getPrice();

        //places disponibles ici + mapper et dto de representation
        List<RepresentationDTO> representations = representationMapper.toDTOList(show.getRepresentations());

        String location = show.getLocation() != null ? show.getLocation().getDesignation() : null;

        // Création de la map des collaborateurs : rôle → liste d'artistes
        Map<String, List<ArtistDTO>> collaborateurs = new HashMap<>();
        for (ArtistType artistType : show.getArtistTypes()) {
            String role = artistType.getType().getType(); // e.g. "auteur"
            ArtistDTO dto = new ArtistDTO(
                    artistType.getArtist().getId(),
                    artistType.getArtist().getFirstname(),
                    artistType.getArtist().getLastname()
            );
            collaborateurs.computeIfAbsent(role, r -> new ArrayList<>()).add(dto);
        }



        Set<String> tags = show.getTags() != null
                ? show.getTags().stream().map(Tag::getTag).collect(Collectors.toSet())
                : Set.of();

        return new ShowDTO(
                show.getId(),
                show.getTitle(),
                show.getPosterUrl(),
                show.getCreatedAt(),
                show.isBookable(),
                show.getDescription(),
                location,
                tags,
                prices,
                minPrice,
                show.getRepresentations().size(),
                representations,
                collaborateurs
        );
    }
}
