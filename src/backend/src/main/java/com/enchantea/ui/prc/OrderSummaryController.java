package com.enchantea.ui.prc;

import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.prc.OrderSummary;
import com.enchantea.application.prc.OrderSummaryService;
import com.enchantea.application.reg.AccountFacade;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.prc.Charge;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;
import java.time.Instant;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//customer views the summary of a delivered order (UC-PRC-03)
@RestController
@RequestMapping("/api/orders")
public class OrderSummaryController {
    private final OrderSummaryService SUMMARY_SERVICE;
    private final AccountFacade ACCOUNT_FACADE;

    public OrderSummaryController(OrderSummaryService SUMMARY_SERVICE, AccountFacade ACCOUNT_FACADE) {
        this.SUMMARY_SERVICE = SUMMARY_SERVICE;
        this.ACCOUNT_FACADE = ACCOUNT_FACADE;
    }

    @GetMapping("/{id}/summary")
    public OrderSummaryView summary(@PathVariable String id, HttpServletRequest servletRequest) {
        return toView(SUMMARY_SERVICE.summaryFor(currentUser(servletRequest), id));
    }

    //same session -> CurrentUser conversion as OrderController
    private CurrentUser currentUser(HttpServletRequest servletRequest) {
        User user = ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession(false));
        Long restaurantId = user instanceof Restaurant ? user.getUserId() : null;
        return new CurrentUser(user.getUserId(), user.getAccountType(), restaurantId);
    }

    private static OrderSummaryView toView(OrderSummary summary) {
        return new OrderSummaryView(
                summary.orderId(), summary.restaurantId(), summary.restaurantName(),
                summary.lines(), toChargeView(summary.charge()),
                summary.placedAt(), summary.deliveredAt(), summary.closedAt(), summary.finalStatus());
    }

    //old orders placed before pricing have no charge
    private static ChargeView toChargeView(Charge charge) {
        if (charge == null) {
            return null;
        }
        return new ChargeView(
                charge.subtotal(), charge.deliveryFee(), charge.serviceFee(),
                charge.smallOrderSurcharge(), charge.total());
    }

    public record ChargeView(
            double subtotal,
            double deliveryFee,
            double serviceFee,
            double smallOrderSurcharge,
            double total) { }

    public record OrderSummaryView(
            String orderId,
            Long restaurantId,
            String restaurantName,
            List<OrderSummary.Line> lines,
            ChargeView charge,
            Instant placedAt,
            Instant deliveredAt,
            Instant closedAt,
            OrderStatus finalStatus) { }
}