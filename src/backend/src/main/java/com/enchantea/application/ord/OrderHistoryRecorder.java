package com.enchantea.application.ord;
import java.util.List;
import java.util.ArrayList;

//Observer that listens for order events and stores them in memory (R-ORD-05)
public final class OrderHistoryRecorder implements OrderObserver {
    private final List<OrderEvent> HISTORY = new ArrayList<>();

    @Override
    public void onOrderStateChanged(OrderEvent event) {
        HISTORY.add(event);
    }

    //recorded event for a specific order
    public List<OrderEvent> historyFor(String orderId) {
        return HISTORY.stream()
                .filter(event -> event.orderId().equals(orderId))
                .toList();
    }

    public List<OrderEvent> all() {
        return List.copyOf(HISTORY);
    }
}
