package com.reservations.reservations.service;


import com.reservations.reservations.model.Artist;
import com.reservations.reservations.model.ArtistType;
import com.reservations.reservations.model.Type;
import com.reservations.reservations.repository.ArtistTypeRepository;
import com.reservations.reservations.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistTypeService {
    @Autowired
    private ArtistTypeRepository artistTypeRepository;

    @Autowired
    private TypeRepository typeRepository;

    /**
     * Pour alimenter le formulaire “type d’artiste” (Auteur, Comédien, …).
     */
    public List<Type> getAllTypes() {
        return (List<Type>) typeRepository.findAll();
    }

    /**
     * Récupère la liaison ArtistType existante pour cet artist+type
     * ou la crée si elle n’existe pas encore.
     */
    public ArtistType getOrCreate(Artist artist, Long typeId) {
        Type type = typeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Type d’artiste introuvable : " + typeId));
        return artistTypeRepository
                .findByArtistAndType(artist, (Type) type)
                .orElseGet(() -> {
                    ArtistType at = new ArtistType();
                    at.setArtist(artist);
                    at.setType(type);
                    return artistTypeRepository.save(at);
                });
    }
}