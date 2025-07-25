package com.reservations.reservations.model;


import com.github.slugify.Slugify;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String slug;

    private String designation;
    private String address;

    @ManyToOne
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;

    private String website;
    private String phone;

    @OneToMany(targetEntity = Show.class, mappedBy = "location")
    private List<Show> shows = new ArrayList<>();

    @OneToMany(targetEntity = Representation.class, mappedBy = "location")
    private List<Representation> representations = new ArrayList<>();

    @Column(nullable = false)
    private Integer capacity = 0;

    protected Location() {
    }

    public Location(Long id, String slug, String designation, String address, Locality locality, String website, String phone, List<Show> shows, List<Representation> representations) {
        this.id = id;
        this.slug = slug;
        this.designation = designation;
        this.address = address;
        this.locality = locality;
        this.website = website;
        this.phone = phone;
        this.shows = shows;
        this.representations = representations;
        this.capacity = 0;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;

        Slugify slg = new Slugify();

        this.setSlug(slg.slugify(designation));
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality.removeLocation(this);    //déménager de l’ancienne localité
        this.locality = locality;
        this.locality.addLocation(this);        //emménager dans la nouvelle localité
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Show> getShows() {
        return shows;
    }

    public Location addShow(Show show) {
        if (!this.shows.contains(show)) {
            this.shows.add(show);
            show.setLocation(this);
        }

        return this;
    }

    public Location removeShow(Show show) {
        if (this.shows.contains(show)) {
            this.shows.remove(show);
            if (show.getLocation().equals(this)) {
                show.setLocation(null);
            }
        }

        return this;
    }

    public List<Representation> getRepresentations() {
        return representations;
    }

    public Location addRepresentation(Representation representation) {
        if (!this.representations.contains(representation)) {
            this.representations.add(representation);
            representation.setLocation(this);
        }

        return this;
    }

    public Location removeRepresentation(Representation representation) {
        if (this.representations.contains(representation)) {
            this.representations.remove(representation);
            if (representation.getLocation().equals(this)) {
                representation.setLocation(null);
            }
        }

        return this;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", designation='" + designation + '\'' +
                ", address='" + address + '\'' +
                ", locality=" + locality +
                ", website='" + website + '\'' +
                ", phone='" + phone + '\'' +
                ", shows=" + shows.size() +
                ", representations=" + representations.size() +
                '}';
    }
}
