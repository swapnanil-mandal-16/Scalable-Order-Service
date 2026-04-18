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

    @GetMapping("/getbyid")
    public ProductResponseDTO getbyId(@RequestParam long id) {
        return productService.getById(id);
    }
    @GetMapping("/checkstock")
    public boolean checkStock(@RequestBody ProductCheckUpdateDTO productCheckUpdateDTO) {
        return productService.checkStock(productCheckUpdateDTO.getId(), productCheckUpdateDTO.getQuantity());
    }
    @PutMapping("/reducestock")
    public void reduceStock(@RequestBody ProductCheckUpdateDTO productCheckUpdateDTO) {
        productService.reduceStock(productCheckUpdateDTO.getId(), productCheckUpdateDTO.getQuantity());
    }
}
