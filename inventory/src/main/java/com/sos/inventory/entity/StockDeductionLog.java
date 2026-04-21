package com.sos.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stock_deduction_log")
@Data
public class StockDeductionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long productId;
    private Boolean processed;
}
