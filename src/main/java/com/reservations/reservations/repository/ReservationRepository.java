package com.reservations.reservations.repository;
import com.reservations.reservations.model.Reservation;
import com.reservations.reservations.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByUserIdAndStatus(Long userId, String status);

    List<Reservation> findByUser_Id(Long userId);
}
