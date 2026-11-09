package com.enchantea.domain.ord;

import com.enchantea.domain.ord.state.PlacedState;
import com.enchantea.domain.ord.state.AcceptedState;
import com.enchantea.domain.ord.state.CancelledState;
import com.enchantea.domain.ord.state.DeliveredState;
import com.enchantea.domain.ord.state.PickedUpState;
import com.enchantea.domain.ord.state.PreparingState;
import com.enchantea.domain.ord.state.ReadyState;
import com.enchantea.domain.ord.state.RefundedState;
import com.enchantea.domain.ord.state.RejectedState;
import com.enchantea.domain.prc.Charge;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final String ORDER_ID;
    private OrderState status;
    private final Instant PLACED_AT; //Instant data type is better to capture specific moments
    private Instant closedAt;
    private Instant deliveredAt; 
    private String rejectionReason;
    private Charge charge;
    private final String CUSTOMER_ID;
    private final String RESTAURANT_ID;
    private final List<OrderLine> LINES;
    private final List<StateTransition> TRANSITIONS = new ArrayList<>();

    // constructor
    public Order(String OrderId, String CUSTOMER_ID, String RESTAURANT_ID, List<OrderLine> LINES) {
        this(OrderId, CUSTOMER_ID, RESTAURANT_ID, LINES, Instant.now(), OrderStatus.PLACED, null, null, null, null);
    }

    private Order(
            String orderId,
            String customerId,
            String restaurantId,
            List<OrderLine> lines,
            Instant placedAt,
            OrderStatus orderStatus,
            Instant closedAt,
            Instant deliveredAt,
            String rejectionReason,
            Charge charge) {
        this.ORDER_ID = Objects.requireNonNull(orderId, "id");
        this.CUSTOMER_ID = Objects.requireNonNull(customerId, "CUSTOMER_ID");
        this.RESTAURANT_ID = Objects.requireNonNull(restaurantId, "RESTAURANT_ID");
        this.LINES = List.copyOf(lines);
        if (this.LINES.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one line");
        }
        this.PLACED_AT = Objects.requireNonNull(placedAt, "placedAt");
        this.status = stateFor(orderStatus);
        this.closedAt = closedAt;
        this.deliveredAt = deliveredAt;
        this.rejectionReason = rejectionReason;
        this.charge = charge;
    }

    // is called when order is placed
    public static Order create(String CUSTOMER_ID, String RESTAURANT_ID, List<OrderLine> LINES) {
        return new Order(UUID.randomUUID().toString(), CUSTOMER_ID, RESTAURANT_ID, LINES); //UUID stands for 'Universally Unique Identifier'
    }

    public static Order rehydrate(
            String orderId,
            String customerId,
            String restaurantId,
            List<OrderLine> lines,
            Instant placedAt,
            OrderStatus status,
            Instant closedAt,
            Instant deliveredAt,
            String rejectionReason,
            Charge charge) {
        return new Order(
                orderId, customerId, restaurantId, lines, placedAt, status, closedAt, deliveredAt,
                rejectionReason, charge);
    }

    //getters
    public String id() { return ORDER_ID; }
    public String customerId() { return CUSTOMER_ID; }
    public String restaurantId() { return RESTAURANT_ID; }
    public List<OrderLine> lines() { return LINES; }
    public Instant placedAt() { return PLACED_AT; }
    public Instant closedAt() { return closedAt; }
    public Instant deliveredAt() { return deliveredAt; }
    public OrderStatus status() { return status.status(); }
    public String rejectionReason() { return rejectionReason; }
    public List<StateTransition> transitions() { return List.copyOf(TRANSITIONS); }
    public Charge charge() { return charge; }

    // attaches the computed cost breakdown when the order is placed (R-PRC-01)
    public void attachCharge(Charge charge) {
        this.charge = Objects.requireNonNull(charge, "charge");
    }

    // calculates the order's total (before fees and taxes)
    public double total() {
        double total = 0;
        for (OrderLine line : LINES) {
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
        TRANSITIONS.add(transition);
        // keeps the delivery time even if the order is refunded later (R-PRC-03). This needs to be added bc when an order goes from delievered -> refunded, closedAt is overwritten even when the delivery time needs to be saved for the order summary.
        if (transition.toStatus() == OrderStatus.DELIVERED) {
            deliveredAt = transition.timestamp();
        }
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

    private static OrderState stateFor(OrderStatus orderStatus) {
        return switch (Objects.requireNonNull(orderStatus, "status")) {
            case PLACED -> new PlacedState();
            case ACCEPTED -> new AcceptedState();
            case PREPARING -> new PreparingState();
            case READY -> new ReadyState();
            case PICKED_UP -> new PickedUpState();
            case DELIVERED -> new DeliveredState();
            case REFUNDED -> new RefundedState();
            case REJECTED -> new RejectedState();
            case CANCELLED -> new CancelledState();
        };
    }
}