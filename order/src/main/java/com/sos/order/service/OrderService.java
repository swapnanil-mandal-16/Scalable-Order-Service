package com.sos.order.service;

import com.sos.order.dto.OrderRequestDTO;
import com.sos.order.dto.OrderResponseDTO;
import com.sos.order.dto.OrderUpdateRequestDTO;
import com.sos.order.entity.Order;
import com.sos.order.entity.OrderItem;
import com.sos.order.mapper.OrderMapper;
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
        return OrderMapper.toResponseDto(newOrder);

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

    public OrderResponseDTO updateOrder(OrderUpdateRequestDTO order) {
        // Logic to update an order
        Order existingOrder = orderRepository.findById(order.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setStatus(order.getStatus());
        existingOrder = orderRepository.save(existingOrder);
        return OrderMapper.toResponseDto(existingOrder);
    }
    public void cancelOrder(Long orderId) {
        // Logic to delete an order
        // if order exists, and its not Completed, then cancel it, else throw exception
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if(existingOrder.getStatus().equalsIgnoreCase("COMPLETED")) {
            throw new RuntimeException("Cannot delete a completed order");
        }
        existingOrder.setStatus("CANCELLED");
        orderRepository.save(existingOrder);
    }
    public OrderResponseDTO findOrderById(Long orderId) {
        // Logic to find an order by ID
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderMapper.toResponseDto(existingOrder);
    }

}
