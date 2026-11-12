package com.enchantea.ui.dlv;

import com.enchantea.application.dlv.CourierDutyService;
import com.enchantea.application.ord.OrderApplicationService;
import com.enchantea.application.reg.AccountFacade;
import com.enchantea.domain.dlv.Assignment;
import com.enchantea.domain.dlv.AssignmentStatus;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/courier")
public class CourierController {
    private final AccountFacade ACCOUNT_FACADE;
    private final CourierDutyService COURIER_DUTY_SERVICE;
    private final OrderApplicationService ORDER_APPLICATION_SERVICE;
    private final UserRepository USER_REPOSITORY;

    public CourierController(
            AccountFacade accountFacade,
            CourierDutyService courierDutyService,
            OrderApplicationService orderApplicationService,
            UserRepository userRepository) {

        this.ACCOUNT_FACADE = accountFacade;
        this.COURIER_DUTY_SERVICE = courierDutyService;
        this.ORDER_APPLICATION_SERVICE = orderApplicationService;
        this.USER_REPOSITORY = userRepository;
    }

    @GetMapping("/assignment")
    public CourierView state(HttpServletRequest request) {
        return viewFor(currentCourierId(request));
    }

    @PostMapping("/online")
    public CourierView goOnline(HttpServletRequest request) {
        Long courierId = currentCourierId(request);
        COURIER_DUTY_SERVICE.goOnline(courierId);
        return viewFor(courierId);
    }

    @PostMapping("/offline")
    public CourierView goOffline(HttpServletRequest request) {
        Long courierId = currentCourierId(request);
        COURIER_DUTY_SERVICE.goOffline(courierId);
        return viewFor(courierId);
    }

    @PostMapping("/offer/accept")
    public CourierView acceptOffer(HttpServletRequest request) {
        Long courierId = currentCourierId(request);
        COURIER_DUTY_SERVICE.acceptOffer(courierId);
        return viewFor(courierId);
    }

    @PostMapping("/offer/decline")
    public CourierView declineOffer(HttpServletRequest request) {
        Long courierId = currentCourierId(request);
        COURIER_DUTY_SERVICE.declineOffer(courierId);
        return viewFor(courierId);
    }

    @PostMapping("/pickup")
    public CourierView confirmPickup(HttpServletRequest request) {
        Long courierId = currentCourierId(request);
        COURIER_DUTY_SERVICE.confirmPickup(courierId);
        return viewFor(courierId);
    }

    @PostMapping("/delivery")
    public CourierView confirmDelivery(HttpServletRequest request) {
        Long courierId = currentCourierId(request);
        COURIER_DUTY_SERVICE.confirmDelivery(courierId);
        return viewFor(courierId);
    }

    private Long currentCourierId(HttpServletRequest request) {
        User user = ACCOUNT_FACADE.requireLoggedIn(request.getSession(false));
        if (!(user instanceof Courier courier)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Courier account required");
        }
        return courier.getUserId();
    }

    private CourierView viewFor(Long courierId) {
        User user = ACCOUNT_FACADE.findById(courierId);
        if (!(user instanceof Courier courier)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Courier account required");
        }

        Assignment assignment = courier.getAssignment();
        DeliveryView offer = null;
        DeliveryView active = null;
        if (assignment != null && assignment.getOrderId() != null) {
            DeliveryView delivery = deliveryView(assignment);
            if (assignment.getStatus() == AssignmentStatus.OFFERED) {
                offer = delivery;
            } else if (assignment.getStatus() == AssignmentStatus.ACCEPTED) {
                active = delivery;
            }
        }
        return new CourierView(courier.getStatus(), active, offer);
    }

    private DeliveryView deliveryView(Assignment assignment) {
        Order order = ORDER_APPLICATION_SERVICE.get(assignment.getOrderId());
        User restaurantUser = USER_REPOSITORY.findById(order.restaurantId()).orElse(null);
        User customerUser = USER_REPOSITORY.findById(order.customerId()).orElse(null);

        String shop = restaurantUser instanceof Restaurant restaurant
                ? restaurant.getName()
                : "Restaurant " + order.restaurantId();
        String dropoff = customerUser instanceof Customer customer
                ? format(customer.getDeliveryAddress())
                : "Customer " + order.customerId();

        return new DeliveryView(order.id(), shop, dropoff, assignment.getExpiresAt());
    }

    private static String format(Location location) {
        return location == null ? "Address unavailable" : location.getLat() + ", " + location.getLng();
    }

    public record CourierView(CourierStatus courierStatus, DeliveryView active, DeliveryView offer) { }

    public record DeliveryView(String orderId, String shop, String dropoff, Instant expiresAt) { }
}
