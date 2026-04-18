package com.sos.inventory.service;

import com.sos.inventory.dto.ProductRequestDTO;
import com.sos.inventory.dto.ProductResponseDTO;
import com.sos.inventory.entity.Product;
import com.sos.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setPrice(productRequestDTO.getPrice());
        product.setName(productRequestDTO.getName());
        product.setQuantity(productRequestDTO.getQuantity());
        productRepository.save(product);
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setPrice(product.getPrice());
        productResponseDTO.setQuantity(product.getQuantity());
        return productResponseDTO;
    }

    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setPrice(product.getPrice());
        productResponseDTO.setQuantity(product.getQuantity());
        return productResponseDTO;
    }

    public boolean checkStock(long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getQuantity() >= quantity;
    }
    public void reduceStock(long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }
}
