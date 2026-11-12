package com.enchantea.application.dlv;

import com.enchantea.domain.dlv.Assignment;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class CourierAssignmentStore {
    private final ConcurrentMap<Long, Assignment> ASSIGNMENTS = new ConcurrentHashMap<>();

    public Assignment get(Long courierId) {
        return ASSIGNMENTS.get(courierId);
    }

    public boolean hasAssignment(Long courierId) {
        return ASSIGNMENTS.containsKey(courierId);
    }

    public boolean offer(Long courierId, Assignment assignment) {
        return ASSIGNMENTS.putIfAbsent(courierId, assignment) == null;
    }

    public Assignment remove(Long courierId) {
        return ASSIGNMENTS.remove(courierId);
    }

    public List<Long> courierIdsForOrder(String orderId) {
        return ASSIGNMENTS.entrySet().stream()
                .filter(entry -> orderId.equals(entry.getValue().getOrderId()))
                .map(java.util.Map.Entry::getKey)
                .toList();
    }
}
