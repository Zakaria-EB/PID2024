package com.reservations.reservations.dto;


import java.util.List;

public class ArtistTypeDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private List<String> types;

    public ArtistTypeDTO() {

    }

    public ArtistTypeDTO(Long id, String firstname, String lastname, List<String> types) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.types = types;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
