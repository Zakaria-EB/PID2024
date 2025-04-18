package com.reservations.reservations.repository;

import com.reservations.reservations.model.Location;
import com.reservations.reservations.model.Representation;
import com.reservations.reservations.model.Show;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RepresentationRepository extends CrudRepository<Representation, Long> {
    List<Representation> findByShow(Show show);
    List<Representation> findByLocation(Location location);
    List<Representation> findByWhen(LocalDateTime when);
}