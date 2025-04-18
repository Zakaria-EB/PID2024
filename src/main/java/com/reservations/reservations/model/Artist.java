package com.reservations.reservations.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;

    @ManyToMany(mappedBy = "artists")
    @JsonIgnore
    private List<Type> types = new ArrayList<>();

    public List<Type> getTypes() {
        return types;
    }

    public Artist addType(Type type) {
        if (!this.types.contains(type)) {
            this.types.add(type);
            type.addArtist(this);
        }
        return this;
    }

    public Artist removeType(Type type) {
        if (this.types.contains(type)) {
            this.types.remove(type);
            type.getArtists().remove(this);
        }
        return this;
    }
}
