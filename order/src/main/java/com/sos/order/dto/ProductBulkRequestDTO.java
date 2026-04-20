package com.sos.order.dto;

import lombok.Data;

@Data
public class ProductBulkRequestDTO {
    private long productId;
    private int quantity;
}
