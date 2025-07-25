package com.reservations.reservations.model;

import lombok.Data;

@Data
public class CartItem {
    private Long representationId;
    private Long priceId;
    private int quantity;
    private String label; // date/heure de la représentation
    private double unitPrice;

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
