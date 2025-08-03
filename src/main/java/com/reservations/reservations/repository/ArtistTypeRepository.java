package com.reservations.reservations.repository;

import com.reservations.reservations.model.Artist;
import com.reservations.reservations.model.ArtistType;
import jakarta.persistence.metamodel.Type;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArtistTypeRepository extends CrudRepository<ArtistType, Long> {
    Optional<ArtistType> findByArtistAndType(Artist artist, Type type);
}