package com.reservations.reservations.repository;

import com.reservations.reservations.model.Locality;
import org.springframework.data.repository.CrudRepository;

public interface LocalityRepository extends CrudRepository<Locality, Long> {
    Locality findByPostalCode(String postalCode);
    Locality findByLocality(String locality);
}