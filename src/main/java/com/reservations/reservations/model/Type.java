package com.reservations.reservations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "types")
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le type ne peut pas être vide")
    private String type;

    @ManyToMany
    @JoinTable(
            name = "artist_type",
            joinColumns = @JoinColumn(name = "type_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id"))
    private List<Artist> artists = new ArrayList<>();

    public Type(String type) {
        this.type = type;
    }

    public Type addArtist(Artist artist) {
        if (!this.artists.contains(artist)) {
            this.artists.add(artist);
            artist.getTypes().add(this);
        }
        return this;
    }

    public Type removeArtist(Artist artist) {
        if (this.artists.contains(artist)) {
            this.artists.remove(artist);
            artist.getTypes().remove(this);
        }
        return this;
    }

    @Override
    public String toString() {
        return "Type [id=" + id + ", type=" + type + "]";
    }
}