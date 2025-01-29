package be.iccbxl.pid.reservationsspringboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "representation_reservation")
public class RepresentationReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "representation_id")
    private Representation representation;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "price_id")
    private Price price;

    private int quantity;
}
