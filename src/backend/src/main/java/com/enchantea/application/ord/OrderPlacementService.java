package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.application.prc.PricingCalculator;
import com.enchantea.application.promo.PromoService;
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
    private final PromoService PROMO_SERVICE;

    public OrderPlacementService(
            CommandValidator VALIDATOR,
            RestaurantQuery RESTAURANT_QUERY,
            PricingCalculator PRICING_CALCULATOR,
            PromoService PROMO_SERVICE) {
        this.VALIDATOR = VALIDATOR;
        this.RESTAURANT_QUERY = RESTAURANT_QUERY;
        this.PRICING_CALCULATOR = PRICING_CALCULATOR;
        this.PROMO_SERVICE = PROMO_SERVICE;
    }

    public Charge quote(CurrentUser user, OrderCommands.PlaceOrder command) {
        VALIDATOR.validate(CommandType.PLACE_ORDER, user, command);
        return applyPromoIfPresent(PRICING_CALCULATOR.computeCharge(buildLines(command)), command);
    }

    public Order createOrder(CurrentUser user, OrderCommands.PlaceOrder command) {
        VALIDATOR.validate(CommandType.PLACE_ORDER, user, command);
        List<OrderLine> lines = buildLines(command);

        Order order = Order.create(user.id(), command.restaurantId(), lines);
        Charge charge = applyPromoIfPresent(PRICING_CALCULATOR.computeCharge(lines), command);
        order.attachCharge(charge);
        return order;
    }

    // R-PROMO-01: at most one promo code per order, validated at placement time.
    private Charge applyPromoIfPresent(Charge charge, OrderCommands.PlaceOrder command) {
        if (command.promoCode() == null || command.promoCode().isBlank()) {
            return charge;
        }
        double discount = PROMO_SERVICE.resolveDiscount(command.promoCode(), charge.subtotal());
        return charge.withDiscount(discount);
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
