package com.sos.inventory.controller;

import com.sos.inventory.dto.ProductCheckUpdateDTO;
import com.sos.inventory.dto.ProductRequestDTO;
import com.sos.inventory.dto.ProductResponseDTO;
import com.sos.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ProductResponseDTO createProduct(@RequestBody ProductRequestDTO product) {
        return productService.create(product);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable long id) {
        return productService.getById(id);
    }
    @GetMapping("/{id}/check")
    public boolean checkStock(@PathVariable long id, @RequestParam int quantity) {
        return productService.checkStock(id, quantity);
    }
    @PutMapping("/{id}/reduce")
    public void reduceStock(@PathVariable long id, @RequestParam int quantity) {
        productService.reduceStock(id, quantity);
    }
    @PutMapping("/{id}/increase")
    public void increaseStock(@PathVariable long id, @RequestParam int quantity) {
        productService.increaseStock(id, quantity);
    }
}
