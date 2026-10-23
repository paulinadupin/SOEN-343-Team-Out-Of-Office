package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import org.springframework.stereotype.Component;

@Component
public class CommandValidationChain implements CommandValidator {
    private final CommandCheck FIRST;

    public CommandValidationChain(OrderRepository orders, RestaurantQuery restaurantQuery) {
        FIRST = new LoggedInCheck();
        FIRST.then(new RoleCheck())
                .then(new OwnershipCheck(orders))
                .then(new RestaurantRulesCheck(orders, restaurantQuery));
    }

    @Override
    public void validate(CommandType type, CurrentUser user, Object command) {
        FIRST.check(type, user, command);
    }
}
