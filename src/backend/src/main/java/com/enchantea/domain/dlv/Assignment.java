package com.enchantea.domain.dlv;

import java.time.Duration;
import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Assignment {
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status")
    private AssignmentStatus status;
    @Column(name = "assigned_order_id")
    private String orderId;
    @Column(name = "offer_created_at")
    private Instant offeredAt;
    @Column(name = "offer_expires_at")
    private Instant expiresAt;
    @Column(name = "offer_accepted_at")
    private Instant acceptedAt;

    private static final Duration TIME_LIMIT = Duration.ofSeconds(60);
    private static final Duration STALL_THRESHOLD = Duration.ofMinutes(5);

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
        acceptedAt = Instant.now();
    }

    public boolean isStalled() {
        return status == AssignmentStatus.ACCEPTED
                && acceptedAt != null
                && Instant.now().isAfter(acceptedAt.plus(STALL_THRESHOLD));
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
