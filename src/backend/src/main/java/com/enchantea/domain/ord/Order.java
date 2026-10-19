package com.enchantea.domain.ord;

import com.enchantea.domain.ord.state.PlacedState;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final String orderId;
    private OrderState status;
    private final Instant placedAt; //Instant data type is better to capture specific moments
    private Instant closedAt;
    private String rejectionReason;
    private final String customerId;
    private final String restaurantId;
    private final List<OrderLine> lines;
    private final List<StateTransition> transitions = new ArrayList<>();
    
    // constructor
    public Order(String OrderId, String customerId, String restaurantId, List<OrderLine> lines) {
        this.orderId = Objects.requireNonNull(OrderId, "id");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.restaurantId = Objects.requireNonNull(restaurantId, "restaurantId");
        this.lines = List.copyOf(lines);
        if (this.lines.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one line");
        }
        this.placedAt = Instant.now();
        this.status = new PlacedState(); //first state when order is created
    }

    // is called when order is placed
    public static Order create(String customerId, String restaurantId, List<OrderLine> lines) {
        return new Order(UUID.randomUUID().toString(), customerId, restaurantId, lines); //UUID stands for 'Universally Unique Identifier'
    }

    //getters
    public String id() { return orderId; }
    public String customerId() { return customerId; }
    public String restaurantId() { return restaurantId; }
    public List<OrderLine> lines() { return lines; }
    public Instant placedAt() { return placedAt; }
    public Instant closedAt() { return closedAt; }
    public OrderStatus status() { return status.status(); }
    public String rejectionReason() { return rejectionReason; }
    public List<StateTransition> transitions() { return List.copyOf(transitions); }

    // calculates the order's total (before fees and taxes)
    public double total() {
        double total = 0;
        for (OrderLine line : lines) {
            total += line.total();
        }
        return total;
    }

    // when restaurant accepts order
    public OrderStatus accept() { return apply(status.accept()); }

    //when restaurant rejects order
    public OrderStatus reject(String reason) {
        OrderState nextState = status.reject();
        this.rejectionReason = requireReason(reason); // see below for the requireReason function
        return apply(nextState);
    }

    // when customer is cancelling
    public OrderStatus cancelByCustomer() {return apply(status.cancelByCustomer()); }
    //when operator is cancelling
    public OrderStatus cancelByOperator() {return apply(status.cancelByOperator()); }
    // when restaurant is preparing order
    public OrderStatus prepare() { return apply(status.prepare()); }
    // when restaurant marks order as ready
    public OrderStatus markReady() { return apply(status.markReady()); }
    // when order is picked up by courier
    public OrderStatus pickUp() { return apply(status.pickUp()); }
    //when order is delivered up by courier
    public OrderStatus deliver() { return apply(status.deliver()); }
    // when order is refunded
    public OrderStatus refund() { return apply(status.refund()); }

    // enforces the order lifecycle (R-ORD-04)
    private OrderStatus apply(OrderState nextState) {
        status = nextState;
        return status.status();
    }

    // records transitions (R-ORD-05)
    public void recordTransition(StateTransition transition) {
        transitions.add(transition);
        // if at the last state of the order lifecycle, then the order is closed
        if (transition.toStatus() == OrderStatus.DELIVERED
                || transition.toStatus() == OrderStatus.REJECTED
                || transition.toStatus() == OrderStatus.CANCELLED
                || transition.toStatus() == OrderStatus.REFUNDED) {
            closedAt = transition.timestamp();
        }
    }

    //verifies if a reason was provided
    private String requireReason(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("A reason is required");
        }
        return value;
    }
}
