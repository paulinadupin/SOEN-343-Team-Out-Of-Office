package com.enchantea.foundation.ord;

import com.enchantea.domain.ord.OrderStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class PersistedOrder {
    @Id
    private String id;
    @Column(nullable = false)
    private Long customerId;
    @Column(nullable = false)
    private Long restaurantId;
    @Column(nullable = false)
    private Instant placedAt;
    private Instant closedAt;
    private Instant deliveredAt;
    private String rejectionReason;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    private List<PersistedOrderLine> lines = new ArrayList<>();

    @Embedded
    private PersistedCharge charge;

    protected PersistedOrder() {
    }

    public PersistedOrder(
            String id,
            Long customerId,
            Long restaurantId,
            Instant placedAt,
            Instant closedAt,
            Instant deliveredAt,
            String rejectionReason,
            OrderStatus status,
            List<PersistedOrderLine> lines,
            PersistedCharge charge) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.placedAt = placedAt;
        this.closedAt = closedAt;
        this.deliveredAt = deliveredAt;
        this.rejectionReason = rejectionReason;
        this.status = status;
        this.lines = new ArrayList<>(lines);
        this.charge = charge;
    }

    public String id() { return id; }
    public Long customerId() { return customerId; }
    public Long restaurantId() { return restaurantId; }
    public Instant placedAt() { return placedAt; }
    public Instant closedAt() { return closedAt; }
    public Instant deliveredAt() { return deliveredAt; }
    public String rejectionReason() { return rejectionReason; }
    public OrderStatus status() { return status; }
    public List<PersistedOrderLine> lines() { return lines; }
    public PersistedCharge charge() { return charge; }
}