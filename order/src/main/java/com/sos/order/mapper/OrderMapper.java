package com.sos.order.mapper;

import com.sos.order.dto.OrderItemDTO;
import com.sos.order.dto.OrderResponseDTO;
import com.sos.order.entity.Order;
import com.sos.order.entity.OrderItem;

import java.util.ArrayList;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponseDTO toResponseDto(Order order) {
        if (order == null) return null;

        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getId());
        response.setUsername(order.getUsername());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(String.valueOf(order.getStatus()));

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem oi : order.getOrderItems()) {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setProductId(oi.getProductId());
                dto.setQuantity(oi.getQuantity());
                dto.setUnitPrice(oi.getUnitPrice());
                itemDTOs.add(dto);
            }
        }
        response.setOrderItemDTOList(itemDTOs);
        return response;
    }

    public static OrderItemDTO toItemDto(OrderItem orderItem) {
        if (orderItem == null) return null;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(orderItem.getProductId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());

        return dto;
    }
}

