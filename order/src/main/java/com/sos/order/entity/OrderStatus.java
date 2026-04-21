package com.sos.order.entity;

public enum OrderStatus {

    PENDING {
        public boolean canTransitionTo(OrderStatus next) {
            return next == CONFIRMED || next == FAILED;
        }
    },
    CONFIRMED {
        public boolean canTransitionTo(OrderStatus next) {
            return next == COMPLETED || next == CANCELLED;
        }
    },
    COMPLETED,
    FAILED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        return false;
    }
}
