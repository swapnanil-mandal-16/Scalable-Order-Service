package com.sos.inventory.dto;

import lombok.Data;

@Data
public class ProductBulkRequestDTO {
    private long productId;
    private int quantity;
}
