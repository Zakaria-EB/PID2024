package com.reservations.reservations.repository;


import com.reservations.reservations.model.Price;
import org.springframework.data.repository.CrudRepository;

public interface PriceRepository extends CrudRepository<Price, Long> {
    // Implémenter les méthodes de recherche...
}
