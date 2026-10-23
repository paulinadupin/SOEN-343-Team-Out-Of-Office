package com.enchantea.ui.ord;

import com.enchantea.application.reg.AccountFacade;
import com.enchantea.application.ord.OrderApplicationService;
import com.enchantea.application.ord.OrderCommands;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;
import java.time.Instant;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderApplicationService SERVICE;
    private final AccountFacade ACCOUNT_FACADE;

    public OrderController(OrderApplicationService SERVICE, AccountFacade ACCOUNT_FACADE) {
        this.SERVICE = SERVICE;
        this.ACCOUNT_FACADE = ACCOUNT_FACADE;
    }

    @PostMapping
    public OrderView place(@RequestBody PlaceRequest request, HttpServletRequest servletRequest) {
        OrderCommands.PlaceOrder command = new OrderCommands.PlaceOrder(
                request.restaurantId(), request.lines());
        return toView(SERVICE.place(currentUser(servletRequest), command));
    }

    @PostMapping("/{id}/accept")
    public OrderView accept(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.accept(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/reject")
    public OrderView reject(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.reject(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/cancel/customer")
    public OrderView cancelByCustomer(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.cancelByCustomer(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/cancel/operator")
    public OrderView cancelByOperator(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.cancelByOperator(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/prepare")
    public OrderView prepare(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.prepare(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/ready")
    public OrderView markReady(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.markReady(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/pick-up")
    public OrderView pickUp(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.pickUp(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/deliver")
    public OrderView deliver(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.deliver(currentUser(servletRequest), action(id, request)));
    }

    @PostMapping("/{id}/refund")
    public OrderView refund(
            @PathVariable String id, @RequestBody ActionRequest request, HttpServletRequest servletRequest) {
        return toView(SERVICE.refund(currentUser(servletRequest), action(id, request)));
    }

    @GetMapping("/{id}")
    public OrderView get(@PathVariable String id, HttpServletRequest servletRequest) {
        currentUser(servletRequest);
        return toView(SERVICE.get(id));
    }

    @GetMapping
    public List<OrderView> all(HttpServletRequest servletRequest) {
        currentUser(servletRequest);
        return SERVICE.allOrders().stream().map(OrderController::toView).toList();
    }

    @GetMapping("/restaurant")
    public List<OrderView> restaurantOrders(HttpServletRequest servletRequest) {
        CurrentUser user = currentUser(servletRequest);
        if (user.restaurantId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Restaurant account required");
        }
        return SERVICE.ordersForRestaurant(user.restaurantId()).stream()
                .map(OrderController::toView)
                .toList();
    }

    private CurrentUser currentUser(HttpServletRequest servletRequest) {
        User user = ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession(false));
        AccountType accountType = user.getAccountType();
        String restaurantId = user instanceof Restaurant ? user.getUserId().toString() : null;
        return new CurrentUser(user.getUserId().toString(), accountType, restaurantId);
    }

    private OrderCommands.OrderAction action(String id, ActionRequest request) {
        return new OrderCommands.OrderAction(id, request.reason());
    }

    private static OrderView toView(Order order) {
        List<LineView> lines = order.lines().stream()
                .map(line -> new LineView(line.itemId(), line.quantity(), line.unitPrice()))
                .toList();

        return new OrderView(
                order.id(), order.customerId(), order.restaurantId(), lines,
                order.total(), order.status(), order.placedAt(), order.closedAt(), order.rejectionReason());
    }

    public record PlaceRequest(
            String restaurantId,
            List<OrderCommands.RequestedLine> lines) { }

    public record ActionRequest(String reason) { }

    public record LineView(String itemId, int quantity, double unitPrice) { }

    public record OrderView(
            String id,
            String customerId,
            String restaurantId,
            List<LineView> lines,
            double total,
            OrderStatus status,
            Instant placedAt,
            Instant closedAt,
            String rejectionReason) { }
}
