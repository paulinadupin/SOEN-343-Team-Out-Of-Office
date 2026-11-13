package com.enchantea.application.prc;

import com.enchantea.application.ord.OrderCommands;
import com.enchantea.application.ord.OrderNotFoundException;
import com.enchantea.application.ord.OrderRepository;
import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CommandValidator;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//builds the order summary a customer sees after delivery (UC-PRC-03)
@Service
public class OrderSummaryService {
    private final OrderRepository ORDERS;
    private final CommandValidator VALIDATOR;
    private final RestaurantQuery RESTAURANT_QUERY;

    public OrderSummaryService(OrderRepository ORDERS, CommandValidator VALIDATOR, RestaurantQuery RESTAURANT_QUERY) {
        this.ORDERS = ORDERS;
        this.VALIDATOR = VALIDATOR;
        this.RESTAURANT_QUERY = RESTAURANT_QUERY;
    }

    @Transactional(readOnly = true)
    public OrderSummary summaryFor(CurrentUser user, String orderId) {
        //only the customer who placed the order can see it
        VALIDATOR.validate(CommandType.VIEW_ORDER_SUMMARY, user, new OrderCommands.OrderAction(orderId, null));
        Order order = ORDERS.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.status() != OrderStatus.DELIVERED && order.status() != OrderStatus.REFUNDED) {
            throw new CommandRejectedException("The order summary is available once the order is delivered");
        }

        // RestaurantQuery uses Long ids; OrderSummary takes a String id, converted below
        Long restaurantId = order.restaurantId();
        List<OrderSummary.Line> lines = order.lines().stream()
                .map(line -> new OrderSummary.Line(
                        line.itemId(),
                        RESTAURANT_QUERY.itemName(restaurantId, line.itemId()).orElse(line.itemId()),
                        line.quantity(),
                        line.unitPrice(),
                        line.total()))
                .toList();

        return new OrderSummary(
                order.id(),
                String.valueOf(restaurantId),
                RESTAURANT_QUERY.restaurantName(restaurantId).orElse(String.valueOf(restaurantId)),
                lines,
                order.charge(),
                order.placedAt(),
                order.deliveredAt(),
                order.closedAt(),
                order.status());
    }
}