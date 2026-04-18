package com.sos.order.service;

import com.sos.order.client.InventoryClient;
import com.sos.order.dto.OrderRequestDTO;
import com.sos.order.dto.OrderResponseDTO;
import com.sos.order.dto.OrderUpdateRequestDTO;
import com.sos.order.dto.ProductResponseDTO;
import com.sos.order.entity.Order;
import com.sos.order.entity.OrderItem;
import com.sos.order.mapper.OrderMapper;
import com.sos.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    @Autowired
    public OrderService(OrderRepository orderRepository,  InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO order) {
        // Logic to create an order
        Order newOrder = new Order();
        List<OrderItem> orderItems = validateAndPrepareItems(order);
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                        .getName();
        newOrder.setUsername(username);
        newOrder.setTotalAmount(calculatePrice(orderItems));
        for (OrderItem item : orderItems) {
            item.setOrder(newOrder);
        }
        newOrder.setOrderItems(orderItems);
        newOrder.setStatus("CREATED");
        reduceStock(orderItems);
        newOrder = orderRepository.save(newOrder);
        return OrderMapper.toResponseDto(newOrder);

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

    public List<OrderItem> validateAndPrepareItems(OrderRequestDTO order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (var item : order.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            boolean inStock = false;
            try {
                inStock = inventoryClient.checkStock(item.getProductId(), item.getQuantity());
            } catch (Exception e){
                throw new RuntimeException("Inventory Service Error for item " + item.getProductId() + " " + item.getQuantity());
            }
            if(!inStock) {
                throw new RuntimeException("Product with id " + item.getProductId() + " is out of stock");
            }
            try {
                ProductResponseDTO productResponseDTO = inventoryClient.getById(item.getProductId());
                orderItem.setUnitPrice(productResponseDTO.getPrice());
            }catch (Exception e){
                throw new RuntimeException("Inventory Service Error for item " + item.getProductId() + " " + item.getQuantity());
            }
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    public double calculatePrice(List<OrderItem> orderItems) {
        double price = 0.0;
        for (OrderItem orderItem : orderItems) {
            price += orderItem.getUnitPrice() * orderItem.getQuantity();
        }
        return price;
    }
    public void reduceStock(List<OrderItem> orderItems) {
        List<OrderItem> processed = new ArrayList<>();

        try {
            for (OrderItem orderItem : orderItems) {
                inventoryClient.reduceStock(
                        orderItem.getProductId(),
                        orderItem.getQuantity()
                );
                processed.add(orderItem);
            }
        } catch (Exception e) {
            rollbackStock(processed);
            throw new RuntimeException("Stock reduction failed, rolled back", e);
        }
    }

    public void rollbackStock(List<OrderItem> processed) {
        for (OrderItem orderItem : processed) {
            try {
                inventoryClient.increaseStock(
                        orderItem.getProductId(),
                        orderItem.getQuantity()
                );
            } catch (Exception e) {
                // Log the failure to rollback, but continue with other rollbacks
                System.err.println("Failed to rollback stock for product " + orderItem.getProductId() + ": " + e.getMessage());
            }
        }
    }
}
