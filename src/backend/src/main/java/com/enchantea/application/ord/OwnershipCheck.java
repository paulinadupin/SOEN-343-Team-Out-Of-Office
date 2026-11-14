package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.ord.Order;
import java.util.Set;

public class OwnershipCheck extends CommandCheck {
    private static final Set<CommandType> RESTAURANT_COMMANDS = Set.of(
            CommandType.ACCEPT_ORDER,
            CommandType.REJECT_ORDER,
            CommandType.PREPARE_ORDER,
            CommandType.MARK_ORDER_READY);

    private static final Set<CommandType> CUSTOMER_COMMANDS = Set.of(
            CommandType.CANCEL_ORDER_CUSTOMER,
            CommandType.VIEW_ORDER_SUMMARY);

    private static final Set<CommandType> COURIER_COMMANDS = Set.of(
            CommandType.DELIVER_ORDER);

    private final OrderRepository ORDERS;

    public OwnershipCheck(OrderRepository ORDERS) {
        this.ORDERS = ORDERS;
    }

    @Override
    protected void verify(CommandType type, CurrentUser user, Object command) {
        if (RESTAURANT_COMMANDS.contains(type)) {
            Order order = load(command);
            if (!order.restaurantId().equals(user.restaurantId())) {
                throw new CommandRejectedException("This order belongs to another restaurant");
            }
        } else if (CUSTOMER_COMMANDS.contains(type)) {
            Order order = load(command);
            if (!order.customerId().equals(user.id())) {
                throw new CommandRejectedException("This order belongs to another customer");
            }
        } else if (COURIER_COMMANDS.contains(type)) {
            Order order = load(command);
            if (!user.id().equals(order.courierId())) {
                throw new CommandRejectedException("This order is not assigned to this courier");
            }
        }
    }

    private Order load(Object command) {
        String orderId = ((OrderCommands.OrderAction) command).orderId();
        return ORDERS.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
