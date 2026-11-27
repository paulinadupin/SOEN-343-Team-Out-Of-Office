package com.enchantea.application.ord;
import java.util.List;

//request data used by the order use cases.
public final class OrderCommands {
    private OrderCommands() { }

    //one menu item and how many the customer wants.
    public record RequestedLine(String itemId, int quantity) { }

    //the restaurant and items the customer chose when placing an order, with an optional promo code.
    public record PlaceOrder(Long restaurantId, List<RequestedLine> lines, String promoCode) {
        public PlaceOrder {
            lines = List.copyOf(lines);
        }

        public PlaceOrder(Long restaurantId, List<RequestedLine> lines) {
            this(restaurantId, lines, null);
        }
    }

    //used for actions on an existing order. A reason is used when rejecting an order.
    public record OrderAction(String orderId, String reason) { }
}
