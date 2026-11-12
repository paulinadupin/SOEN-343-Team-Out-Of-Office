package com.enchantea.domain.dlv;

import java.time.Duration;
import java.time.Instant;

public class Assignment {
    private AssignmentStatus status;
    private String orderId;
    private Instant offeredAt;
    private Instant expiresAt;

    private static final Duration TIME_LIMIT = Duration.ofSeconds(60);

    protected Assignment() {
    }

    public Assignment(AssignmentStatus status, String orderId, Instant offeredAt) {
        this.status = status;
        this.orderId = orderId;
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

    public void declineOffer(){
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

    public String getOrderId() {
        return orderId;
    }

    
    
    
}
