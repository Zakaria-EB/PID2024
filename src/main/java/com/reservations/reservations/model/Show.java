package com.reservations.reservations.model;



import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@EntityListeners(Show.AuditListener.class)
@Table(name = "shows")
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)

    private boolean bookable;

    @Column(unique = true)
    private String slug;

    private String title;
    private String description;

    @Column(name = "poster_url")
    private String posterUrl;

    /**
     * Lieu de création du spectacle
     */
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;


    @ManyToMany
    @JoinTable(
            name = "show_price",
            joinColumns = @JoinColumn(name = "show_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    private List<Price> prices = new ArrayList<>();

    /**
     * Date de création du spectacle
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de modification du spectacle
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static class AuditListener {
        @PrePersist
        public void setCreatedAndUpdated(Show s) {
            LocalDateTime now = LocalDateTime.now();
            s.createdAt = now;
            s.updatedAt = now;
        }

        @PreUpdate
        public void setUpdated(Show s) {
            s.updatedAt = LocalDateTime.now();
        }
    }

    @OneToMany(mappedBy = "show",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Representation> representations = new ArrayList<>();

    @ManyToMany(mappedBy = "shows")
    private List<ArtistType> artistTypes = new ArrayList<>();

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "show_tag",
            joinColumns = @JoinColumn(name = "show_id"),

            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Show() {
    }

    public Show(String title, String description, String posterUrl, Location location, boolean bookable, List<Price> prices, List<Representation> representations) {
        Slugify slg = new Slugify();

        this.slug = slg.slugify(title);
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.location = location;
        this.bookable = bookable;
        this.prices = prices;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.representations = representations;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    private void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

        Slugify slg = new Slugify();

        this.setSlug(slg.slugify(title));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Location getLocation() {
        return location;
    }


    public void setLocation(Location newLocation) {
        if (Objects.equals(this.location, newLocation)) {
            return; // déjà à jour, on sort
        }
        Location old = this.location;
        this.location = newLocation;
        // Retirer de l’ancienne
        if (old != null && old.getShows().contains(this)) {
            old.getShows().remove(this);
        }
        // Ajouter à la nouvelle
        if (newLocation != null && !newLocation.getShows().contains(this)) {
            newLocation.getShows().add(this);
        }
    }

    /**
     * Un spectacle est réservable si **au moins une** représentation a des places dispo
     */
    @Transient
    public boolean hasAvailableSeats() {
        return representations.stream().anyMatch(r -> r.getAvailableSeats() > 0);
    }

    public void setBookable(boolean bookable) {
        this.bookable = bookable;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Representation> getRepresentations() {
        return representations;
    }

    public Show addRepresentation(Representation representation) {
        if (!this.representations.contains(representation)) {
            this.representations.add(representation);
            representation.setShow(this);
        }

        return this;
    }

    public Show removeRepresentation(Representation representation) {
        if (this.representations.contains(representation)) {
            this.representations.remove(representation);
            if (representation.getLocation().equals(this)) {
                representation.setLocation(null);
            }
        }

        return this;
    }

    public Set<Tag> getTags() {
        if (this.tags == null) {
            this.tags = new HashSet<>();
        }
        return tags;
    }

    /**
     * Get the performances (artists in a type of collaboration) for the show
     */
    public List<ArtistType> getArtistTypes() {
        return artistTypes;
    }

    public Show addArtistType(ArtistType artistType) {
        if (!this.artistTypes.contains(artistType)) {
            this.artistTypes.add(artistType);
            //artistType.addShow(this);
        }

        return this;
    }

    public Show removeArtistType(ArtistType artistType) {
        if (this.artistTypes.contains(artistType)) {
            this.artistTypes.remove(artistType);
            artistType.getShows().remove(this);
        }

        return this;
    }

    @Override
    public String toString() {
        return "Show [id=" + id + ", slug=" + slug + ", title=" + title
                + ", description=" + description + ", posterUrl=" + posterUrl + ", location="
                + location + ", bookable=" + bookable + ", prices=" + prices.size()
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", representations=" + representations.size() + "]";
    }

    public String getAuthorsAsString() {
        if (artistTypes == null || artistTypes.isEmpty()) return "Inconnu";

        return artistTypes.stream()
                .map(at -> {
                    Artist artist = at.getArtist();
                    return artist.getFirstname() + " " + artist.getLastname();
                })
                .distinct()
                .collect(Collectors.joining(", "));
    }

    @Transient
    public String getFullLocation() {
        if (location == null) return "Lieu inconnu";

        String place = location.getDesignation();
        String city = location.getLocality() != null ? location.getLocality().getLocality() : "Ville inconnue";

        return place + ", " + city;
    }

    @Transient
    public String getNextRepresentationDateFormatted() {
        return representations.stream()
                .filter(r -> r.getScheduledAt().isAfter(LocalDateTime.now()))
                .map(r -> r.getScheduledAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .findFirst()
                .orElse("Aucune date");
    }

}