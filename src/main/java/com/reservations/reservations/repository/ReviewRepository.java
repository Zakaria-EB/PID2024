package com.reservations.reservations.repository;


import com.reservations.reservations.model.Review;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    List<Review> findByShowId(Long showId);
}