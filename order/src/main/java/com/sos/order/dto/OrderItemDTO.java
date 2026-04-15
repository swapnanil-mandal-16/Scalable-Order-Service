package com.sos.order.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productId;
    private int quantity;
    private double unitPrice;
}
