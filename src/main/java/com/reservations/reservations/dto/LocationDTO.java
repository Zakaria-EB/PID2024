package com.reservations.reservations.dto;

import lombok.*;

@Data
@Getter
@Setter
public class LocationDTO {
    private long id;
    private String slug;
    private String description;
    private String address;
    private String website;
    private String phone;
    private String capacity;
    private Long localityId;


}
