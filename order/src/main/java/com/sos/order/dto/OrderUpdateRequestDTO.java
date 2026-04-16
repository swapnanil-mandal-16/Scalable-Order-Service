package com.sos.order.dto;

import lombok.Data;

@Data
public class OrderUpdateRequestDTO {
        private Long orderId;
        private String status;

}
