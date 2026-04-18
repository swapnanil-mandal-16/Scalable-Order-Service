package com.sos.inventory.dto;

import lombok.Data;

@Data
public class ProductRequestDTO {
    private String name;
    private Double price;
    private Long quantity;
}
