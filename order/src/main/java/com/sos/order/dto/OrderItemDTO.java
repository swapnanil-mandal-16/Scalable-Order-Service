package com.sos.order.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private long productId;
    private int quantity;
    private double unitPrice;
}
