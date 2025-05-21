package com.reservations.reservations.repository;

import com.reservations.reservations.model.Troupe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TroupeRepository extends JpaRepository<Troupe, Long> {

        Optional<Troupe> findByName(String name);
        boolean existsByName(String name);
    }
