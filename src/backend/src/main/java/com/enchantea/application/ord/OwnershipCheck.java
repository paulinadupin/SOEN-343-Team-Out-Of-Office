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

    private final OrderRepository orders;

    public OwnershipCheck(OrderRepository orders) {
        this.orders = orders;
    }

    @Override
    protected void verify(CommandType type, CurrentUser user, Object command) {
        if (RESTAURANT_COMMANDS.contains(type)) {
            Order order = load(command);
            if (!order.restaurantId().equals(user.restaurantId())) {
                throw new CommandRejectedException("This order belongs to another restaurant");
            }
        } else if (type == CommandType.CANCEL_ORDER_CUSTOMER) {
            Order order = load(command);
            if (!order.customerId().equals(user.id())) {
                throw new CommandRejectedException("This order belongs to another customer");
            }
        }
    }

    private Order load(Object command) {
        String orderId = ((OrderCommands.OrderAction) command).orderId();
        return orders.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
