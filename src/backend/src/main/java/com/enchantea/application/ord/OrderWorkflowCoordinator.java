package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CourierAssignment;
import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.PreparationQueue;
import com.enchantea.domain.ord.Order;
import org.springframework.stereotype.Service;

/** Applies the queue and courier side effects that accompany order workflow changes. */
@Service
public class OrderWorkflowCoordinator {
    private final PreparationQueue PREPARATION_QUEUE;
    private final DispatchQueue DISPATCH_QUEUE;
    private final CourierAssignment COURIER_ASSIGNMENT;

    public OrderWorkflowCoordinator(
            PreparationQueue PREPARATION_QUEUE,
            DispatchQueue DISPATCH_QUEUE,
            CourierAssignment COURIER_ASSIGNMENT) {
        this.PREPARATION_QUEUE = PREPARATION_QUEUE;
        this.DISPATCH_QUEUE = DISPATCH_QUEUE;
        this.COURIER_ASSIGNMENT = COURIER_ASSIGNMENT;
    }

    public void accepted(Order order) {
        PREPARATION_QUEUE.enqueue(order.restaurantId(), order);
    }

    public void cancelledByOperator(Order order) {
        PREPARATION_QUEUE.remove(order.restaurantId(), order.id());
        DISPATCH_QUEUE.removeFromDispatch(order.id());
        COURIER_ASSIGNMENT.removeOrderAssignment(order.id());
    }

    public void ready(Order order) {
        PREPARATION_QUEUE.remove(order.restaurantId(), order.id());
        DISPATCH_QUEUE.add(order);
    }

    public void dispatchNext() {
        COURIER_ASSIGNMENT.dispatchNext();
    }

    public void pickedUp(Order order) {
        DISPATCH_QUEUE.removeFromDispatch(order.id());
    }
}
