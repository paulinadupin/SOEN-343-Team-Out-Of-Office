package com.enchantea.foundation.dlv;

import com.enchantea.domain.dlv.HandoffType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "delivery_handoffs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_handoff_order_type", columnNames = {"order_id", "handoff_type"})
})
public class PersistedHandoff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "courier_id", nullable = false)
    private Long courierId;

    @Enumerated(EnumType.STRING)
    @Column(name = "handoff_type", nullable = false)
    private HandoffType type;

    @Column(name = "occurred_at", nullable = false)
    private Instant timestamp;

    protected PersistedHandoff() {
    }

    public PersistedHandoff(String orderId, Long courierId, HandoffType type, Instant timestamp) {
        this.orderId = orderId;
        this.courierId = courierId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String orderId() { return orderId; }
    public Long courierId() { return courierId; }
    public HandoffType type() { return type; }
    public Instant timestamp() { return timestamp; }
}
