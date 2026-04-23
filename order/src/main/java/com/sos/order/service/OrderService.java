package com.sos.order.service;

import com.sos.order.client.InventoryClient;
import com.sos.order.dto.*;
import com.sos.order.entity.Order;
import com.sos.order.entity.OrderItem;
import com.sos.order.entity.OrderStatus;
import com.sos.order.mapper.OrderMapper;
import com.sos.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        List<ProductBulkResponseDTO> response = getBulkProductsWithResilience(mapToBulkRequest(order));
        if (response.isEmpty()) {
            throw new RuntimeException("Invalid inventory response");
        }
        List<OrderItem> orderItems = buildOrderItems(response, order);
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
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder = orderRepository.save(newOrder);

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(newOrder.getId());
        event.setItems(newOrder.getOrderItems().stream().map(OrderMapper::toItemDto).collect(Collectors.toList()));
        inventoryClient.bulkReduceStock(mapToBulkRequest(newOrder));

        newOrder.setStatus(OrderStatus.CONFIRMED);
        newOrder = orderRepository.save(newOrder);
        return OrderMapper.toResponseDto(newOrder);

    }
    public OrderResponseDTO updateOrder(OrderUpdateRequestDTO order) {
        // Logic to update an order
        Order existingOrder = orderRepository.findById(order.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setStatus(OrderStatus.valueOf(order.getStatus()));
        existingOrder = orderRepository.save(existingOrder);
        return OrderMapper.toResponseDto(existingOrder);
    }
    public void cancelOrder(Long orderId) {
        // Logic to delete an order
        // if order exists, and its not Completed, then cancel it, else throw exception
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if(!existingOrder.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new RuntimeException("Invalid order status transition.");
        }
        existingOrder.setStatus(OrderStatus.CANCELLED);
        inventoryClient.bulkIncreaseStock(mapToBulkRequest(existingOrder));
        orderRepository.save(existingOrder);
    }
    public OrderResponseDTO findOrderById(Long orderId) {
        // Logic to find an order by ID
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderMapper.toResponseDto(existingOrder);
    }

    public double calculatePrice(List<OrderItem> orderItems) {
        double price = 0.0;
        for (OrderItem orderItem : orderItems) {
            price += orderItem.getUnitPrice() * orderItem.getQuantity();
        }
        return price;
    }

    public List<ProductBulkRequestDTO> mapToBulkRequest(OrderRequestDTO order) {
        List<ProductBulkRequestDTO> list = new ArrayList<>();

        for (var item : order.getOrderItems()) {
            ProductBulkRequestDTO dto = new ProductBulkRequestDTO();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            list.add(dto);
        }
        return list;
    }

    public List<ProductBulkRequestDTO> mapToBulkRequest(Order order) {
        List<ProductBulkRequestDTO> list = new ArrayList<>();
        for (var item : order.getOrderItems()) {
            ProductBulkRequestDTO dto = new ProductBulkRequestDTO();
            dto.setOrderId(order.getId());
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            list.add(dto);
        }
        return list;
    }

    public List<OrderItem> buildOrderItems(
            List<ProductBulkResponseDTO> responses,
            OrderRequestDTO order) {

        Map<Long, ProductBulkResponseDTO> map = responses.stream()
                .collect(Collectors.toMap(
                        ProductBulkResponseDTO::getProductId,
                        r -> r
                ));

        List<OrderItem> items = new ArrayList<>();

        for (var reqItem : order.getOrderItems()) {

            ProductBulkResponseDTO res = map.get(reqItem.getProductId());

            if (res == null || !res.isInStock()) {
                throw new RuntimeException("Product out of stock: " + reqItem.getProductId());
            }

            OrderItem item = new OrderItem();
            item.setProductId(reqItem.getProductId());
            item.setQuantity(reqItem.getQuantity());
            item.setUnitPrice(res.getPrice());

            items.add(item);
        }

        return items;
    }

    @Retry(name = "inventoryService", fallbackMethod = "bulkFallback")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "bulkFallback")
    public List<ProductBulkResponseDTO> getBulkProductsWithResilience(
            List<ProductBulkRequestDTO> request) {

        return inventoryClient.checkAndGetProducts(request);
    }
    public List<ProductBulkResponseDTO> bulkFallback(
            List<ProductBulkRequestDTO> request,
            Exception ex) {

        throw new RuntimeException(
                "Inventory service unavailable. Please try again later.", ex
        );
    }

    public OrderResponseDTO completeOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(!order.getStatus().canTransitionTo(OrderStatus.COMPLETED)) {
            throw new RuntimeException("Invalid order status transition.");
        }

        order.setStatus(OrderStatus.COMPLETED);

        return OrderMapper.toResponseDto(orderRepository.save(order));
    }
}
