package com.enchantea.domain.dlv;

import java.time.Instant;

public class Handoff {
    private String orderId;
    private long courierId;
    private HandoffType type;
    private Instant timestamp;
    
    public Handoff(String orderId, long courierId, HandoffType type, Instant timestamp) {
        this.orderId = orderId;
        this.courierId = courierId;
        this.type = type;
        this.timestamp = timestamp;
    }

    // --- getters ---

    public String getOrderId() {
        return orderId;
    }

    public long getCourierId() {
        return courierId;
    }

    public HandoffType getType() {
        return type;
    }
    public Instant getTimestamp() {
        return timestamp;
    }

    
}
