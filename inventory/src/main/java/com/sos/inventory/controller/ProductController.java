package com.sos.inventory.controller;

import com.sos.inventory.dto.*;
import com.sos.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/bulk")
    public List<ProductBulkResponseDTO> checkAndGetProducts(
            @RequestBody List<ProductBulkRequestDTO> requests) {
        return productService.checkAndGetProducts(requests);
    }

    @PutMapping("/bulkreduce")
    public void bulkReduceStock(
            @RequestBody List<ProductBulkRequestDTO> requests) {
        productService.bulkReduce(requests);
    }

    @PutMapping("/bulkincrease")
    public void bulkIncreaseStock(
            @RequestBody List<ProductBulkRequestDTO> requests) {
        productService.bulkIncrease(requests);
    }


}
