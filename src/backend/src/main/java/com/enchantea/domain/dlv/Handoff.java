package com.enchantea.domain.dlv;

import java.time.Instant;

public class Handoff {
    private HandoffType type;
    private Instant timestamp;
    
    public Handoff(HandoffType type, Instant timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public HandoffType getType() {
        return type;
    }
    public Instant getTimestamp() {
        return timestamp;
    }

    
}
