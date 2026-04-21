package com.sos.inventory.repository;

import com.sos.inventory.entity.StockDeductionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDeductionLogRepository extends JpaRepository<StockDeductionLog, Long> {
    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
}
