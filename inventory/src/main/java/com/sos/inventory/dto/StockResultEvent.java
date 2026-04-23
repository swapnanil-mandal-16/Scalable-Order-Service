package com.sos.inventory.dto;

import lombok.Data;

@Data
public class StockResultEvent {
    private Long orderId;
    private boolean success;
    private String message;
}
