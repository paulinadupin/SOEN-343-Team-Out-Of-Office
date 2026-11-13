package com.enchantea.application.dash;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enchantea.application.ord.OrderRepository;
import com.enchantea.application.reg.CourierResponse;
import com.enchantea.application.reg.CustomerResponse;
import com.enchantea.application.reg.RestaurantResponse;
import com.enchantea.domain.dlv.AssignmentStatus;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;

@Service
public class MapService {
    private final UserRepository USERS;
    private final OrderRepository ORDERS;

    public MapService(UserRepository users, OrderRepository orders) {
        this.USERS = users;
        this.ORDERS = orders;
    }

    @Transactional(readOnly = true)
    public MapResponse restaurantMap(Long restaurantId) {
        User restaurantUser = USERS.findById(restaurantId)
        .orElseThrow(()-> new IllegalArgumentException("Restaurant not found"));

    if(!(restaurantUser instanceof Restaurant restaurant)){
        throw new IllegalArgumentException("User is not a restaurant owner");
    }

    List<String> restaurantOrderIds = ORDERS.findByRestaurantId(restaurantId)
        .stream()
        .filter(order -> order.status() == OrderStatus.READY)
        .map(Order::id)
        .toList();

    List<CourierResponse> couriers = USERS.findAll().stream()
        .filter(Courier.class::isInstance)
        .map(Courier.class::cast)
        .filter(courier -> courier.getAssignment() != null)
        .filter(courier -> courier.getAssignment().getOrderId() != null)
        .filter(courier -> restaurantOrderIds.contains(
                courier.getAssignment().getOrderId()))
        .map(CourierResponse::from)
        .toList();

    return new MapResponse(List.of(RestaurantResponse.from(restaurant)), couriers, null);
        
    }

    @Transactional(readOnly = true)
    public MapResponse courierMap(Long courierId) {
        User courierUser = USERS.findById(courierId)
        .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        if(!(courierUser instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        List<CourierResponse> courierResponse = List.of(CourierResponse.from(courier));

        // Return an empty map if the courier has no assigned orders
        if(courier.getAssignment() == null || courier.getAssignment().getOrderId() == null) {
            return new MapResponse(List.of(), courierResponse, null);
        }

        Order courierOrder = ORDERS.findById(courier.getAssignment().getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        //Making into a list to fit the parameters of MapResponse
        List<RestaurantResponse> restaurantResponse = USERS.findAll()
            .stream()
            .filter(user -> user.getUserId().equals(courierOrder.restaurantId()))
            .filter(Restaurant.class::isInstance)
            .map(Restaurant.class::cast)
            .map(RestaurantResponse::from)
            .toList();

        CustomerResponse customerResponse = USERS.findById(courierOrder.customerId())
            .filter(Customer.class::isInstance)
            .map(Customer.class::cast)
            .map(CustomerResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return new MapResponse(restaurantResponse, courierResponse, customerResponse);
        
    }

    @Transactional(readOnly = true)
    public MapResponse customerMap(Long customerId) {
        User customerUser = USERS.findById(customerId)
        .orElseThrow(()-> new IllegalArgumentException("Customer not found"));

        if(!(customerUser instanceof Customer customer)){
            throw new IllegalArgumentException("User is not a customer");
        }

        //Filter to make sure to get the orders only associated with the current customer
        List<Order> customerOrders = ORDERS.findAll()
            .stream()
            .filter(order -> customerId.equals(order.customerId()))
            .filter(order -> order.status() == OrderStatus.READY || order.status() == OrderStatus.PICKED_UP)
            .toList();

        List<Long> restaurantIds = customerOrders
            .stream()
            .map(Order::restaurantId)
            .distinct()
            .toList();

        List<String> orderIds = customerOrders
            .stream()
            .map(Order::id)
            .toList();

        List<RestaurantResponse> restaurants = USERS.findAll()
            .stream()
            .filter(Restaurant.class::isInstance)
            .map(Restaurant.class::cast)
            .filter(restaurant -> restaurantIds.contains(restaurant.getUserId()))
            .map(RestaurantResponse::from)
            .toList();

        List<CourierResponse> couriers = USERS.findAll()
            .stream()
            .filter(Courier.class::isInstance)
            .map(Courier.class::cast)
            .filter(courier -> courier.getAssignment() != null)
            .filter(courier -> courier.getAssignment().getStatus() == AssignmentStatus.ACCEPTED)
            .filter(courier -> orderIds.contains(courier.getAssignment().getOrderId()))
            .map(CourierResponse::from)
            .toList();

        // Show customer's information on the map
        CustomerResponse customerResponse = CustomerResponse.from(customer);

        return new MapResponse(restaurants, couriers, customerResponse);
    }
}
