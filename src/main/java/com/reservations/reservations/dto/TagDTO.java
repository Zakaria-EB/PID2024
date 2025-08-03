package com.reservations.reservations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TagDTO {
    private Long id;
    @NotBlank (message = "Le tag est obligatoire.")
    private String tag;


}
