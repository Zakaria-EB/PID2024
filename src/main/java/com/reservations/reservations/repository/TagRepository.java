package com.reservations.reservations.repository;

import com.reservations.reservations.model.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByTag(String tag);
}
