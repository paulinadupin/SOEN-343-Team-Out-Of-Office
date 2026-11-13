package com.enchantea.foundation.ord;

import org.springframework.data.jpa.repository.JpaRepository;
import com.enchantea.domain.ord.OrderStatus;
import java.util.List;

public interface PersistedOrderRepository extends JpaRepository<PersistedOrder, String> {
    List<PersistedOrder> findByRestaurantId(Long restaurantId);
    List<PersistedOrder> findByStatus(OrderStatus status);
    List<PersistedOrder> findByCustomerId(Long customerId);
    List<PersistedOrder> findByCourierId(Long courierId);
}
