package com.sos.inventory.service;

import com.sos.inventory.dto.ProductBulkRequestDTO;
import com.sos.inventory.dto.ProductBulkResponseDTO;
import com.sos.inventory.dto.ProductRequestDTO;
import com.sos.inventory.dto.ProductResponseDTO;
import com.sos.inventory.entity.Product;
import com.sos.inventory.entity.StockDeductionLog;
import com.sos.inventory.repository.ProductRepository;
import com.sos.inventory.repository.StockDeductionLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final StockDeductionLogRepository stockDeductionLogRepository;
    @Autowired
    public ProductService(ProductRepository productRepository, StockDeductionLogRepository stockDeductionLogRepository) {
        this.productRepository = productRepository;
        this.stockDeductionLogRepository = stockDeductionLogRepository;
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
            reduceStock(req.getOrderId(),req.getProductId(), req.getQuantity());
        }
    }

    public void bulkIncrease(List<ProductBulkRequestDTO> requests) {
        for (ProductBulkRequestDTO req : requests) {
            Product product = productRepository.findById(req.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            product.setQuantity(product.getQuantity() + req.getQuantity());
            productRepository.save(product);
        }
    }

    @Transactional
    public void reduceStock(long orderId, long productId, int quantity) {
        System.out.println("Processing orderId: " + orderId + ", productId: " + productId);
        boolean alreadyProcessed = stockDeductionLogRepository.existsByOrderIdAndProductId(orderId,productId);
        if(alreadyProcessed) return;
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        StockDeductionLog log = new StockDeductionLog();
        log.setOrderId(orderId);
        log.setProductId(productId);
        log.setProcessed(true);
        try {
            stockDeductionLogRepository.save(log);
        } catch (DataIntegrityViolationException e) {
            // another request already processed it
            return;
        }
    }
}
