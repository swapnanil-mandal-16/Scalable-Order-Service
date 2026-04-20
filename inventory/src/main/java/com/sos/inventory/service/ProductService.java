package com.sos.inventory.service;

import com.sos.inventory.dto.ProductBulkRequestDTO;
import com.sos.inventory.dto.ProductBulkResponseDTO;
import com.sos.inventory.dto.ProductRequestDTO;
import com.sos.inventory.dto.ProductResponseDTO;
import com.sos.inventory.entity.Product;
import com.sos.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void increaseStock(long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(product.getQuantity() + quantity);
        productRepository.save(product);
    }

    public List<ProductBulkResponseDTO> checkAndGetProducts(
            List<ProductBulkRequestDTO> requests) {

        List<ProductBulkResponseDTO> response = new ArrayList<>();

        for (ProductBulkRequestDTO req : requests) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductBulkResponseDTO res = new ProductBulkResponseDTO();
            res.setProductId(product.getId());
            res.setPrice(product.getPrice());
            res.setInStock(product.getQuantity() >= req.getQuantity());

            response.add(res);
        }
        return response;
    }

    public void bulkReduce(List<ProductBulkRequestDTO> requests) {
        for (ProductBulkRequestDTO req : requests) {
            Product product = productRepository.findById(req.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getQuantity() < req.getQuantity()) {
                throw new RuntimeException("Not enough stock for product " + req.getProductId());
            }
            product.setQuantity(product.getQuantity() - req.getQuantity());
            productRepository.save(product);
        }
    }

    public void bulkIncrease(List<ProductBulkRequestDTO> requests) {
        for (ProductBulkRequestDTO req : requests) {
            Product product = productRepository.findById(req.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            product.setQuantity(product.getQuantity() + req.getQuantity());
            productRepository.save(product);
        }
    }
}
