package com.enchantea.application.ord;

import java.util.List;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;

public class RestaurantRulesCheck extends CommandCheck {
    private final OrderRepository ORDERS;
    private final RestaurantQuery RESTAURANT_QUERY;

    public RestaurantRulesCheck(OrderRepository ORDERS, RestaurantQuery RESTAURANT_QUERY) {
        this.ORDERS = ORDERS;
        this.RESTAURANT_QUERY = RESTAURANT_QUERY;
    }

    @Override
    protected void verify(CommandType type, CurrentUser user, Object command) {
        if (type == CommandType.PLACE_ORDER) {
            verifyPlaceOrder((OrderCommands.PlaceOrder) command);
        } else if (type == CommandType.ACCEPT_ORDER) {
            verifyAcceptOrder((OrderCommands.OrderAction) command);
        }
    }

    private void verifyPlaceOrder(OrderCommands.PlaceOrder place) {
        requireRestaurant(place.restaurantId());
        requireOpen(place.restaurantId());
        requireAtLeastOneLine(place.lines());
        place.lines().forEach(line -> requireItemAvailable(place.restaurantId(), line.itemId()));
    }

    private void verifyAcceptOrder(OrderCommands.OrderAction accept) {
        requireOpen(restaurantIdOf(accept.orderId()));
    }

    private Long restaurantIdOf(String orderId) {
        return ORDERS.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId))
                .restaurantId();
    }

    private void requireRestaurant(Long restaurantId) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new CommandRejectedException("A restaurant is required");
        }
    }

    private void requireOpen(Long restaurantId) {
        if (!RESTAURANT_QUERY.isOpen(restaurantId)) {
            throw new CommandRejectedException("The restaurant is closed");
        }
    }

    private void requireAtLeastOneLine(List<OrderCommands.RequestedLine> lines) {
        if (lines.isEmpty()) {
            throw new CommandRejectedException("An order needs at least one item");
        }
    }

    private void requireItemAvailable(Long restaurantId, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new CommandRejectedException("An item is required");
        }
        if (!RESTAURANT_QUERY.isItemAvailable(restaurantId, itemId)) {
            throw new CommandRejectedException("Item is not available: " + itemId);
        }
    }
}