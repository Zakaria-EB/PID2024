package com.reservations.reservations.mapper;







import com.reservations.reservations.dto.LocalityDTO;
import com.reservations.reservations.model.Locality;
import com.reservations.reservations.model.Location;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//supprimer note perso
// le controller prendra les info d'ici, si jamais on veut changer quelque chose plus tard
// c'est centralisé, plus besoins de passer par les models. limiter les données envoyé aussi

@Component
public class LocationShowMapper {


    public LocalityDTO mapLocalityToLocalityDTO(Locality locality) {
        if (locality == null) {
            return null;
        }
        LocalityDTO localityDTO = new LocalityDTO();
        localityDTO.setId(locality.getId());
        localityDTO.setPostalCode(locality.getPostalCode());
        localityDTO.setLocality( locality.getLocality() );

        if (locality.getLocations() != null) {
            List<String> placeNames = locality.getLocations()
                    .stream()
                    .map(Location::getDesignation)
                    .collect(Collectors.toList());
            localityDTO.setPlaces(placeNames);
        }

        return localityDTO;
    }






}
