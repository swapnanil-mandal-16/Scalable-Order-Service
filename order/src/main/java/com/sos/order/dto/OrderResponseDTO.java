package com.sos.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private String username;
    private String status;
    private double totalAmount;
    private List<OrderItemDTO> orderItemDTOList;
}
