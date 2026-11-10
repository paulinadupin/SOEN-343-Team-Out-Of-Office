package com.enchantea.domain.dlv;

import java.time.Duration;
import java.time.Instant;

import com.enchantea.domain.ord.Order;

public class Assignment {
    private AssignmentStatus status;
    private Order order;
    private Instant offeredAt;
    private Instant expiresAt;

    private final Duration TIME_LIMIT = Duration.ofSeconds(60);

    public Assignment(AssignmentStatus status, Order order, Instant offeredAt) {
        this.status = status;
        this.order = order;
        this.offeredAt = offeredAt;
        expiresAt = offeredAt.plus(TIME_LIMIT);
    }

    public boolean isExpired(){
        if (Instant.now().isAfter(expiresAt)){
            return true;
        }
        return false;
    }

    public void acceptOffer(){
        status=AssignmentStatus.ACCEPTED;

    }

    public void declineOfffer(){
        status=AssignmentStatus.DECLINED;

    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public Instant getOfferedAt() {
        return offeredAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Order getOrder() {
        return order;
    }

    
    
    
}
