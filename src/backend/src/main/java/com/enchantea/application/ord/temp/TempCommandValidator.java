package com.enchantea.application.ord.temp;

import com.enchantea.application.ord.OrderCommands;
import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import org.springframework.stereotype.Component;

/** TEMP validator. Replace this with Person B's real validation chain. */
@Component
public class TempCommandValidator implements CommandValidator {
    private final RestaurantQuery restaurantQuery;

    public TempCommandValidator(RestaurantQuery restaurantQuery) {
        this.restaurantQuery = restaurantQuery;
    }

    @Override
    public void validate(CommandType type, CurrentUser user, Object command) {
        if (user == null || user.id() == null || user.id().isBlank() || user.role() == null) {
            throw new CommandRejectedException("A current user is required");
        }

        if (type != CommandType.PLACE_ORDER) {
            return;
        }

        OrderCommands.PlaceOrder place = (OrderCommands.PlaceOrder) command;
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
