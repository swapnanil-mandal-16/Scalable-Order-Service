package com.sos.inventory.dto;

import lombok.Data;

@Data
public class ProductBulkResponseDTO {
    private Long productId;
    private double price;
    private boolean inStock;
}
