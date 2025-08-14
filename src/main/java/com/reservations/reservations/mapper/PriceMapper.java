package com.reservations.reservations.mapper;

import com.reservations.reservations.dto.PriceDTO;
import com.reservations.reservations.model.Price;

public class PriceMapper {
    public static PriceDTO toPriceDTO(Price price) {
        if (price == null)
            return null;
        return new PriceDTO(
                price.getId(),
                price.getType(),
                price.getPrice(),
                price.getStartDate(),
                price.getEndDate()
        );
    }
}
