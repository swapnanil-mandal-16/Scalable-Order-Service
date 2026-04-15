package com.sos.order.controller;

import com.sos.order.dto.OrderRequestDTO;
import com.sos.order.dto.OrderResponseDTO;
import com.sos.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
