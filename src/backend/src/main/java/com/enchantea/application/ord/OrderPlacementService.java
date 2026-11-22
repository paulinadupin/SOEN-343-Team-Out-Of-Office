package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.application.prc.PricingCalculator;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderLine;
import com.enchantea.domain.prc.Charge;
import java.util.List;
import org.springframework.stereotype.Service;

/** Builds priced order lines and calculates the charge for placement and quotes. */
@Service
public class OrderPlacementService {
    private final CommandValidator VALIDATOR;
    private final RestaurantQuery RESTAURANT_QUERY;
    private final PricingCalculator PRICING_CALCULATOR;

    public OrderPlacementService(
            CommandValidator VALIDATOR,
            RestaurantQuery RESTAURANT_QUERY,
            PricingCalculator PRICING_CALCULATOR) {
        this.VALIDATOR = VALIDATOR;
        this.RESTAURANT_QUERY = RESTAURANT_QUERY;
        this.PRICING_CALCULATOR = PRICING_CALCULATOR;
    }

    public Charge quote(CurrentUser user, OrderCommands.PlaceOrder command) {
        VALIDATOR.validate(CommandType.PLACE_ORDER, user, command);
        return PRICING_CALCULATOR.computeCharge(buildLines(command));
    }

    public Order createOrder(CurrentUser user, OrderCommands.PlaceOrder command) {
        VALIDATOR.validate(CommandType.PLACE_ORDER, user, command);
        List<OrderLine> lines = buildLines(command);

        Order order = Order.create(user.id(), command.restaurantId(), lines);
        order.attachCharge(PRICING_CALCULATOR.computeCharge(lines));
        return order;
    }

    private List<OrderLine> buildLines(OrderCommands.PlaceOrder command) {
        return command.lines().stream()
                .map(line -> new OrderLine(
                        line.itemId(),
                        line.quantity(),
                        RESTAURANT_QUERY.itemPrice(command.restaurantId(), line.itemId())))
                .toList();
    }
}
