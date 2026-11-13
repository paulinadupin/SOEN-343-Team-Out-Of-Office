package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.CourierAssignment;
import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.PreparationQueue;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.application.prc.LedgerRepository;
import com.enchantea.application.prc.PricingCalculator;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderLine;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.ord.StateTransition;
import com.enchantea.domain.prc.Charge;
import com.enchantea.domain.prc.ChargeRecording;
import com.enchantea.domain.prc.LedgerEntryType;
import com.enchantea.domain.prc.RefundRecording;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//runs the order use cases and connects the domain to validation, queues, storage, and events.
public class OrderApplicationService {
    private final OrderRepository ORDERS;
    private final CommandValidator VALIDATOR;
    private final PreparationQueue PREPARATION_QUEUE;
    private final DispatchQueue DISPATCH_QUEUE;
    private final CourierAssignment COURIER_ASSIGNMENT;
    private final RestaurantQuery RESTAURANT_QUERY;
    private final OrderEventSubject EVENT_SUBJECT;
    private final PricingCalculator PRICING_CALCULATOR;
    private final LedgerRepository LEDGER_REPOSITORY;

    public OrderApplicationService(
            OrderRepository ORDERS,
            CommandValidator VALIDATOR,
            PreparationQueue PREPARATION_QUEUE,
            DispatchQueue DISPATCH_QUEUE,
            CourierAssignment COURIER_ASSIGNMENT,
            RestaurantQuery RESTAURANT_QUERY,
            OrderEventSubject EVENT_SUBJECT,
            PricingCalculator PRICING_CALCULATOR,
            LedgerRepository LEDGER_REPOSITORY) {
        this.ORDERS = ORDERS;
        this.VALIDATOR = VALIDATOR;
        this.PREPARATION_QUEUE = PREPARATION_QUEUE;
        this.DISPATCH_QUEUE = DISPATCH_QUEUE;
        this.COURIER_ASSIGNMENT = COURIER_ASSIGNMENT;
        this.RESTAURANT_QUERY = RESTAURANT_QUERY;
        this.EVENT_SUBJECT = EVENT_SUBJECT;
        this.PRICING_CALCULATOR = PRICING_CALCULATOR;
        this.LEDGER_REPOSITORY = LEDGER_REPOSITORY;
    }

    public Charge quote(CurrentUser user, OrderCommands.PlaceOrder command) {
        VALIDATOR.validate(CommandType.PLACE_ORDER, user, command);
        return PRICING_CALCULATOR.computeCharge(buildLines(command));
    }

    @Transactional
    public Order place(CurrentUser user, OrderCommands.PlaceOrder command) {
        //check the request before creating the order
        VALIDATOR.validate(CommandType.PLACE_ORDER, user, command);

        List<OrderLine> lines = buildLines(command);

        Order order = Order.create(user.id(), command.restaurantId(), lines);
        //compute and attach the cost breakdown before the customer confirms (R-PRC-01)
        order.attachCharge(PRICING_CALCULATOR.computeCharge(lines));
        //record the placed state and let observers know the order was created
        OrderEvent event = createEvent(order, null, null);
        ORDERS.save(order);
        EVENT_SUBJECT.publish(event);
        return order;
    }

    @Transactional
    public Order accept(CurrentUser user, OrderCommands.OrderAction command) {
        //accepted ORDERS go to the end of the preparation queue
        VALIDATOR.validate(CommandType.ACCEPT_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.accept();
        PREPARATION_QUEUE.enqueue(order.restaurantId(), order);
        return saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order reject(CurrentUser user, OrderCommands.OrderAction command) {
        //the restaurant gives a reason when it rejects an order
        VALIDATOR.validate(CommandType.REJECT_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.reject(command.reason());
        return saveAndPublish(order, oldStatus, command.reason());
    }

    @Transactional
    public Order cancelByCustomer(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.CANCEL_ORDER_CUSTOMER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.cancelByCustomer();
        return saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order cancelByOperator(CurrentUser user, OrderCommands.OrderAction command) {
        //remove a cancelled order from queues and its courier assignment
        VALIDATOR.validate(CommandType.CANCEL_ORDER_OPERATOR, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.cancelByOperator();
        PREPARATION_QUEUE.remove(order.restaurantId(), order.id());
        DISPATCH_QUEUE.removeFromDispatch(order.id());
        COURIER_ASSIGNMENT.removeOrderAssignment(order.id());
        return saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order prepare(CurrentUser user, OrderCommands.OrderAction command) {
        //only the first order in the preparation queue can be prepared
        VALIDATOR.validate(CommandType.PREPARE_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        Order firstOrder = PREPARATION_QUEUE.peek(order.restaurantId())
                .orElseThrow(() -> new IllegalStateException("Preparation queue is empty"));
        if (!firstOrder.id().equals(order.id())) {
            throw new IllegalStateException("Order is not first in the preparation queue");
        }

        order.prepare();
        return saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order markReady(CurrentUser user, OrderCommands.OrderAction command) {
        //move the first preparing order from preparation to dispatch
        VALIDATOR.validate(CommandType.MARK_ORDER_READY, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        Order firstOrder = PREPARATION_QUEUE.peek(order.restaurantId())
                .orElseThrow(() -> new IllegalStateException("Preparation queue is empty"));
        if (!firstOrder.id().equals(order.id())) {
            throw new IllegalStateException("Order is not first in the preparation queue");
        }

        order.markReady();
        PREPARATION_QUEUE.remove(order.restaurantId(), order.id());
        DISPATCH_QUEUE.add(order);
        Order readyOrder = saveAndPublish(order, oldStatus, null);
        COURIER_ASSIGNMENT.dispatchNext();
        return readyOrder;
    }

    @Transactional
    public Order pickUp(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.PICK_UP_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.pickUp(user.id());
        DISPATCH_QUEUE.removeFromDispatch(order.id());
        return saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order deliver(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.DELIVER_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.deliver();
        LEDGER_REPOSITORY.save(new ChargeRecording().record(order));
        return saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order refund(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.REFUND_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        // check that a delivered was charged so it can be refunded 
        if (order.status() != OrderStatus.DELIVERED) {
            throw new CommandRejectedException("Only a delivered order can be refunded");
        }
        boolean charged = LEDGER_REPOSITORY.findByOrderId(order.id()).stream()
                .anyMatch(entry -> entry.type() == LedgerEntryType.CHARGE);
        if (!charged) {
            throw new CommandRejectedException("Order has no charge to refund");
        }

        order.refund();
        LEDGER_REPOSITORY.save(new RefundRecording().record(order));
        return saveAndPublish(order, oldStatus, null);
    }

    public Order get(String orderId) {
        return load(orderId);
    }

    public List<Order> allOrders() {
        return ORDERS.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> ordersForRestaurant(Long restaurantId) {
        return ORDERS.findByRestaurantId(restaurantId);
    }

    private List<OrderLine> buildLines(OrderCommands.PlaceOrder command) {
        return command.lines().stream()
                .map(line -> new OrderLine(
                        line.itemId(),
                        line.quantity(),
                        RESTAURANT_QUERY.itemPrice(command.restaurantId(), line.itemId())))
                .toList();
    }

    private Order load(String orderId) {
        return ORDERS.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Order saveAndPublish(Order order, OrderStatus oldStatus, String reason) {
        //save the changed order, then send its event to observers
        OrderEvent event = createEvent(order, oldStatus, reason);
        ORDERS.save(order);
        EVENT_SUBJECT.publish(event);
        return order;
    }

    private OrderEvent createEvent(Order order, OrderStatus oldStatus, String reason) {
        //each transition gets its own event id and timestamp
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
