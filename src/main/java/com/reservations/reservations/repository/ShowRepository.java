package com.reservations.reservations.repository;

import com.reservations.reservations.model.Location;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShowRepository extends CrudRepository<Show, Long> {
    Show findBySlug(String slug);

    Show findByTitle(String title);

    List<Show> findByLocation(Location location);

    List<Show> findByTagsContaining(Tag tag);

    @Query("SELECT DISTINCT s FROM Show s " +
            "LEFT JOIN FETCH s.tags " +
            "LEFT JOIN FETCH s.artistTypes " +
            "WHERE s.id = :id")
    Optional<Show> findByIdWithAssociations(@Param("id") Long id);

    @Query("SELECT s FROM Show s WHERE :tag NOT MEMBER OF s.tags")
    List<Show> findByTagsNotContaining(@Param("tag") Tag tag);

    @Query("SELECT DISTINCT s FROM Show s " +
            "JOIN s.representations r " +
            "WHERE s.bookable = true AND r.scheduledAt > CURRENT_TIMESTAMP")
    List<Show> findUpcomingShows();
}
