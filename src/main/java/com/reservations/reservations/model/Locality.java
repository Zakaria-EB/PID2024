package com.reservations.reservations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//@Data
//@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
//@Entity
//@Table(name="localities")
//public class Locality {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//    private String postalCode;
//    private String locality;
//
//    public void removeLocation(Location location) {
//    }
//
//    public void addLocation(Location location) {
//    }
//}

@Entity
@Table(name="localities")
public class Locality {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String postalCode;
    private String locality;

    @OneToMany( targetEntity=Location.class, mappedBy="locality" )
    private List<Location> locations = new ArrayList<>();

    protected Locality() { }

    public Locality(String postalCode, String locality) {
        this.postalCode = postalCode;
        this.locality = locality;
    }

    public Long getId() {
        return id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Locality addLocation(Location location) {
        if(!this.locations.contains(location)) {
            this.locations.add(location);
            location.setLocality(this);
        }

        return this;
    }

    public Locality removeLocation(Location location) {
        if(this.locations.contains(location)) {
            this.locations.remove(location);
            if(location.getLocality().equals(this)) {
                location.setLocality(null);
            }
        }

        return this;
    }

    @Override
    public String toString() {
        return "Locality [id=" + id + ", postalCode=" + postalCode + ", locality=" + locality + "]";
    }

}

