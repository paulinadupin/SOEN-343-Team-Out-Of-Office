package com.enchantea.domain.dlv;

import java.time.Instant;

public class Assignment {
    private AssignmentStatus status;
    private Instant offeredAt;

    public Assignment(AssignmentStatus status, Instant offeredAt) {
        this.status = status;
        this.offeredAt = offeredAt;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public Instant getOfferedAt() {
        return offeredAt;
    }
    
}
