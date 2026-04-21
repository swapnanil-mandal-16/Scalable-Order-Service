package com.sos.order.controller;

import com.sos.order.dto.OrderRequestDTO;
import com.sos.order.dto.OrderResponseDTO;
import com.sos.order.dto.OrderUpdateRequestDTO;
import com.sos.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public OrderResponseDTO save(@RequestBody OrderRequestDTO order) {
        return orderService.createOrder(order);
    }
    @GetMapping("/{orderId}")
    public OrderResponseDTO getOrderById(@PathVariable Long orderId) {
        // Logic to get an order by ID
        return orderService.findOrderById(orderId);
    }
    @PutMapping("/update")
    public OrderResponseDTO updateOrder(@RequestBody OrderUpdateRequestDTO order) {
        // Logic to update an order
        return orderService.updateOrder(order);
    }
    @PutMapping("/cancel/{orderId}")
    public void cancelOrder(@PathVariable Long orderId) {
        // Logic to cancel an order
        orderService.cancelOrder(orderId);
    }

    @PutMapping("/complete/{orderId}")
    public OrderResponseDTO completeOrder(@PathVariable Long orderId) {
        return orderService.completeOrder(orderId);
    }

}
