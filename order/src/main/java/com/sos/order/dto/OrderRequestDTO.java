package com.sos.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private String customerId;
    private List<OrderItemDTO> orderItems;

}
