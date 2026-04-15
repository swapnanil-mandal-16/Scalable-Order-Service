package com.sos.order.service;

import com.sos.order.dto.OrderItemDTO;
import com.sos.order.dto.OrderRequestDTO;
import com.sos.order.dto.OrderResponseDTO;
import com.sos.order.entity.Order;
import com.sos.order.entity.OrderItem;
import com.sos.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO order) {
        // Logic to create an order
        Order newOrder = getNewOrder(order);
        newOrder = orderRepository.save(newOrder);
        // create and return OrderResponseDTO based on the saved order entity
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(newOrder.getId());
        response.setCustomerId(newOrder.getCustomerId());
        response.setTotalAmount(newOrder.getTotalAmount());
        response.setStatus(newOrder.getStatus());

        // build and set order item DTOs on the response
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (newOrder.getOrderItems() != null) {
            for (OrderItem oi : newOrder.getOrderItems()) {
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

    private static @NonNull Order getNewOrder(OrderRequestDTO order) {
        Order newOrder = new Order();
        double totalAmount = 0.0;
        // Set properties of newOrder based on order DTO
        newOrder.setCustomerId(order.getCustomerId());
        newOrder.setStatus("CREATED");

        // create orderItem entities based on order items in the order DTO and save them to the database
        List<OrderItem> orderItems = new ArrayList<>();
        for (var item : order.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            // need to get unit price from inventory microservice based on productId and set it in orderItem
            orderItem.setUnitPrice(1.50); // hardcoded for now, need to get it from inventory microservice
            totalAmount = totalAmount + (orderItem.getUnitPrice()*orderItem.getQuantity());
            orderItem.setOrder(newOrder);
            orderItems.add(orderItem);
        }
        newOrder.setTotalAmount(totalAmount);
        newOrder .setOrderItems(orderItems);
        return newOrder;
    }

    public void updateOrder() {
        // Logic to update an order
    }
    public void deleteOrder() {
        // Logic to delete an order
    }
    public Order findOrderById(Long orderId) {
        return null;
    }

}
