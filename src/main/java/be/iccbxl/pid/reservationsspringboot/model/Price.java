package be.iccbxl.pid.reservationsspringboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
}
