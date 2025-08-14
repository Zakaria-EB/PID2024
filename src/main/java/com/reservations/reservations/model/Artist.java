package com.reservations.reservations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The firstname must not be empty.")
    @Size(min = 2, max = 60, message = "The firstname must be between 2 and 60 characters long.")
    private String firstname;

    @NotBlank(message = "The lastname must not be empty.")
    @Size(min = 2, max = 60, message = "The firstname must be between 2 and 60 characters long.")
    private String lastname;

    @ManyToMany(mappedBy = "artists")
    private List<Type> types = new ArrayList<>();

    // Problème avec lombok !
    public Long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

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

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
