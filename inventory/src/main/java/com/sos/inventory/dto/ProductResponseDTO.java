package com.sos.inventory.dto;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private Double price;
    private Long quantity;
}
