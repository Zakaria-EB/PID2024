package com.reservations.reservations.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class LocalityDTO {
    private Long id;
    private String postalCode;
    private String locality;
    private List<String> places;


}
