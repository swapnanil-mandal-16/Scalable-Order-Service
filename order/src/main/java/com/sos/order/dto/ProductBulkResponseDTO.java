package com.sos.order.dto;

import lombok.Data;

@Data
public class ProductBulkResponseDTO {
    private Long productId;
    private double price;
    private boolean inStock;
}
