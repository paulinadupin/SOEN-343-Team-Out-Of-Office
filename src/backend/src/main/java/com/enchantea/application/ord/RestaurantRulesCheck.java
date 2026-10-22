package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;

public class RestaurantRulesCheck extends CommandCheck {
    private final OrderRepository orders;
    private final RestaurantQuery restaurantQuery;

    public RestaurantRulesCheck(OrderRepository orders, RestaurantQuery restaurantQuery) {
        this.orders = orders;
        this.restaurantQuery = restaurantQuery;
    }

    @Override
    protected void verify(CommandType type, CurrentUser user, Object command) {
        if (type == CommandType.PLACE_ORDER) {
            verifyPlaceOrder((OrderCommands.PlaceOrder) command);
        } else if (type == CommandType.ACCEPT_ORDER) {
            String orderId = ((OrderCommands.OrderAction) command).orderId();
            String restaurantId = orders.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId))
                    .restaurantId();
            if (!restaurantQuery.isOpen(restaurantId)) {
                throw new CommandRejectedException("The restaurant is closed");
            }
        }
    }

    private void verifyPlaceOrder(OrderCommands.PlaceOrder place) {
        if (place.restaurantId() == null || place.restaurantId().isBlank()) {
            throw new CommandRejectedException("A restaurant is required");
        }
        if (!restaurantQuery.isOpen(place.restaurantId())) {
            throw new CommandRejectedException("The restaurant is closed");
        }
        if (place.lines().isEmpty()) {
            throw new CommandRejectedException("An order needs at least one item");
        }

        for (OrderCommands.RequestedLine line : place.lines()) {
            if (line.itemId() == null || line.itemId().isBlank()) {
                throw new CommandRejectedException("An item is required");
            }
            if (!restaurantQuery.isItemAvailable(place.restaurantId(), line.itemId())) {
                throw new CommandRejectedException("Item is not available: " + line.itemId());
            }
        }
    }
}
