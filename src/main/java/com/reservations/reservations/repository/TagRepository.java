package com.reservations.reservations.repository;
import com.reservations.reservations.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByTag(String tag);
}
