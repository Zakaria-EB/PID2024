package be.iccbxl.pid.reservationsspringboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="artiste_type")
public class ArtisteType {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;
}
