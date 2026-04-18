package com.sos.order.client;

import com.sos.order.dto.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "http://localhost:8082")
public interface InventoryClient {
    @GetMapping("/products/{id}")
    ProductResponseDTO getById(@PathVariable("id") long id);

    @GetMapping("/products/{id}/check")
    boolean checkStock(@PathVariable("id") long id, @RequestParam("quantity") int quantity);

    @PutMapping("/products/{id}/reduce")
    void reduceStock(@PathVariable("id") long id, @RequestParam("quantity") int quantity);

    @PutMapping("/products/{id}/increase")
    void increaseStock(@PathVariable("id") long id, @RequestParam("quantity") int quantity);
}
