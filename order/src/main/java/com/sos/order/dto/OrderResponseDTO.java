package com.sos.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private String customerId;
    private String status;
    private double totalAmount;
    private List<OrderItemDTO> orderItemDTOList;
}
