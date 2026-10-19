package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.CourierAssignment;
import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.PreparationQueue;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderLine;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.ord.StateTransition;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
//runs the order use cases and connects the domain to validation, queues, storage, and events.
public class OrderApplicationService {
    private final OrderRepository orders;
    private final CommandValidator validator;
    private final PreparationQueue preparationQueue;
    private final DispatchQueue dispatchQueue;
    private final CourierAssignment courierAssignment;
    private final RestaurantQuery restaurantQuery;
    private final OrderEventSubject eventSubject;

    public OrderApplicationService(
            OrderRepository orders,
            CommandValidator validator,
            PreparationQueue preparationQueue,
            DispatchQueue dispatchQueue,
            CourierAssignment courierAssignment,
            RestaurantQuery restaurantQuery,
            OrderEventSubject eventSubject) {
        this.orders = orders;
        this.validator = validator;
        this.preparationQueue = preparationQueue;
        this.dispatchQueue = dispatchQueue;
        this.courierAssignment = courierAssignment;
        this.restaurantQuery = restaurantQuery;
        this.eventSubject = eventSubject;
    }

    public Order place(CurrentUser user, OrderCommands.PlaceOrder command) {
        //check the request before creating the order
        validator.validate(CommandType.PLACE_ORDER, user, command);

        List<OrderLine> lines = command.lines().stream()
                .map(line -> new OrderLine(
                        line.itemId(),
                        line.quantity(),
                        restaurantQuery.itemPrice(command.restaurantId(), line.itemId())))
                .toList();

        Order order = Order.create(user.id(), command.restaurantId(), lines);
        //record the placed state and let observers know the order was created
        OrderEvent event = createEvent(order, null, null);
        orders.save(order);
        eventSubject.publish(event);
        return order;
    }

    public Order accept(CurrentUser user, OrderCommands.OrderAction command) {
        //accepted orders go to the end of the preparation queue
        validator.validate(CommandType.ACCEPT_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.accept();
        preparationQueue.enqueue(order.restaurantId(), order);
        return saveAndPublish(order, oldStatus, null);
    }

    public Order reject(CurrentUser user, OrderCommands.OrderAction command) {
        //the restaurant gives a reason when it rejects an order
        validator.validate(CommandType.REJECT_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.reject(command.reason());
        return saveAndPublish(order, oldStatus, command.reason());
    }

    public Order cancelByCustomer(CurrentUser user, OrderCommands.OrderAction command) {
        validator.validate(CommandType.CANCEL_ORDER_CUSTOMER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.cancelByCustomer();
        return saveAndPublish(order, oldStatus, null);
    }

    public Order cancelByOperator(CurrentUser user, OrderCommands.OrderAction command) {
        //remove a cancelled order from queues and its courier assignment
        validator.validate(CommandType.CANCEL_ORDER_OPERATOR, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.cancelByOperator();
        preparationQueue.remove(order.restaurantId(), order.id());
        dispatchQueue.removeFromDispatch(order.restaurantId(), order.id());
        courierAssignment.removeOrderAssignment(order.id());
        return saveAndPublish(order, oldStatus, null);
    }

    public Order prepare(CurrentUser user, OrderCommands.OrderAction command) {
        //only the first order in the preparation queue can be prepared
        validator.validate(CommandType.PREPARE_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        Order firstOrder = preparationQueue.peek(order.restaurantId())
                .orElseThrow(() -> new IllegalStateException("Preparation queue is empty"));
        if (!firstOrder.id().equals(order.id())) {
            throw new IllegalStateException("Order is not first in the preparation queue");
        }

        order.prepare();
        return saveAndPublish(order, oldStatus, null);
    }

    public Order markReady(CurrentUser user, OrderCommands.OrderAction command) {
        //move the first preparing order from preparation to dispatch
        validator.validate(CommandType.MARK_ORDER_READY, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        Order firstOrder = preparationQueue.peek(order.restaurantId())
                .orElseThrow(() -> new IllegalStateException("Preparation queue is empty"));
        if (!firstOrder.id().equals(order.id())) {
            throw new IllegalStateException("Order is not first in the preparation queue");
        }

        order.markReady();
        preparationQueue.remove(order.restaurantId(), order.id());
        dispatchQueue.add(order.restaurantId(), order);
        return saveAndPublish(order, oldStatus, null);
    }

    public Order pickUp(CurrentUser user, OrderCommands.OrderAction command) {
        validator.validate(CommandType.PICK_UP_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.pickUp();
        dispatchQueue.removeFromDispatch(order.restaurantId(), order.id());
        return saveAndPublish(order, oldStatus, null);
    }

    public Order deliver(CurrentUser user, OrderCommands.OrderAction command) {
        validator.validate(CommandType.DELIVER_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.deliver();
        return saveAndPublish(order, oldStatus, null);
    }

    public Order refund(CurrentUser user, OrderCommands.OrderAction command) {
        validator.validate(CommandType.REFUND_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.refund();
        return saveAndPublish(order, oldStatus, null);
    }

    public Order get(String orderId) {
        return load(orderId);
    }

    public List<Order> allOrders() {
        return orders.findAll();
    }

    private Order load(String orderId) {
        return orders.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Order saveAndPublish(Order order, OrderStatus oldStatus, String reason) {
        //save the changed order, then send its event to observers
        OrderEvent event = createEvent(order, oldStatus, reason);
        orders.save(order);
        eventSubject.publish(event);
        return order;
    }

    private OrderEvent createEvent(Order order, OrderStatus oldStatus, String reason) {
        //each transition gets its own event ID and timestamp
        String eventId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();

        StateTransition transition = new StateTransition(
                eventId,
                oldStatus,
                order.status(),
                timestamp);
        //save the transition on the order to keep its history
        order.recordTransition(transition);

        return new OrderEvent(
                eventId,
                order.id(),
                order.restaurantId(),
                order.customerId(),
                oldStatus,
                order.status(),
                timestamp,
                reason);
    }
}
