package com.enchantea.foundation.ord;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersistedOrderRepository extends JpaRepository<PersistedOrder, String> {
    List<PersistedOrder> findByRestaurantId(Long restaurantId);
}
