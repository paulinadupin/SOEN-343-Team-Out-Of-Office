package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.prc.Charge;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Coordinates the order use cases and delegates pricing, queue, event, and ledger work. */
@Service
public class OrderApplicationService {
    private final OrderRepository ORDERS;
    private final CommandValidator VALIDATOR;
    private final OrderPlacementService ORDER_PLACEMENT;
    private final PreparationQueueService PREPARATION_QUEUES;
    private final OrderWorkflowCoordinator ORDER_WORKFLOW;
    private final OrderEventRecorder EVENT_RECORDER;
    private final OrderLedgerService ORDER_LEDGER;

    public OrderApplicationService(
            OrderRepository ORDERS,
            CommandValidator VALIDATOR,
            OrderPlacementService ORDER_PLACEMENT,
            PreparationQueueService PREPARATION_QUEUES,
            OrderWorkflowCoordinator ORDER_WORKFLOW,
            OrderEventRecorder EVENT_RECORDER,
            OrderLedgerService ORDER_LEDGER) {
        this.ORDERS = ORDERS;
        this.VALIDATOR = VALIDATOR;
        this.ORDER_PLACEMENT = ORDER_PLACEMENT;
        this.PREPARATION_QUEUES = PREPARATION_QUEUES;
        this.ORDER_WORKFLOW = ORDER_WORKFLOW;
        this.EVENT_RECORDER = EVENT_RECORDER;
        this.ORDER_LEDGER = ORDER_LEDGER;
    }

    public Charge quote(CurrentUser user, OrderCommands.PlaceOrder command) {
        return ORDER_PLACEMENT.quote(user, command);
    }

    @Transactional
    public Order place(CurrentUser user, OrderCommands.PlaceOrder command) {
        Order order = ORDER_PLACEMENT.createOrder(user, command);
        return EVENT_RECORDER.saveAndPublish(order, null, null);
    }

    @Transactional
    public Order accept(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.ACCEPT_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.accept();
        ORDER_WORKFLOW.accepted(order);
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order reject(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.REJECT_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.reject(command.reason());
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, command.reason());
    }

    @Transactional
    public Order cancelByCustomer(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.CANCEL_ORDER_CUSTOMER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.cancelByCustomer();
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order cancelByOperator(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.CANCEL_ORDER_OPERATOR, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.cancelByOperator();
        ORDER_WORKFLOW.cancelledByOperator(order);
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order prepare(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.PREPARE_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        PREPARATION_QUEUES.requireFirstOrder(order);
        order.prepare();
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order markReady(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.MARK_ORDER_READY, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        PREPARATION_QUEUES.requireFirstOrder(order);
        order.markReady();
        ORDER_WORKFLOW.ready(order);
        Order readyOrder = EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
        ORDER_WORKFLOW.dispatchNext();
        return readyOrder;
    }

    @Transactional
    public Order pickUp(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.PICK_UP_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.pickUp(user.id());
        ORDER_WORKFLOW.pickedUp(order);
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order deliver(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.DELIVER_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        order.deliver();
        ORDER_LEDGER.recordCharge(order);
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
    }

    @Transactional
    public Order refund(CurrentUser user, OrderCommands.OrderAction command) {
        VALIDATOR.validate(CommandType.REFUND_ORDER, user, command);
        Order order = load(command.orderId());
        OrderStatus oldStatus = order.status();

        ORDER_LEDGER.refund(order, user.id());
        return EVENT_RECORDER.saveAndPublish(order, oldStatus, null);
    }

    @Transactional(readOnly = true)
    public Order get(String orderId) {
        return load(orderId);
    }

    @Transactional(readOnly = true)
    public List<Order> allOrders() {
        return ORDERS.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> ordersForRestaurant(Long restaurantId) {
        return ORDERS.findByRestaurantId(restaurantId);
    }

    public int preparationQueueSize(Long restaurantId) {
        return PREPARATION_QUEUES.size(restaurantId);
    }

    private Order load(String orderId) {
        return ORDERS.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}