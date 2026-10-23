package com.enchantea.application.ord;
import java.util.ArrayList;
import java.util.List;

//subject in the Observer pattern. It sends each order event to the OBSERVERS that subscribed to it.
public final class OrderEventSubject {
    private final List<OrderObserver> OBSERVERS = new ArrayList<>();

    //add an observer so it gets future order events.
    public void subscribe(OrderObserver observer) {
        OBSERVERS.add(observer);
    }

    //remove an observer so it stops getting order events.
    public void unsubscribe(OrderObserver observer) {
        OBSERVERS.remove(observer);
    }

    //send the event to every observer (for example, OrderHistoryRecorder).
    public void publish(OrderEvent event) {
        for (OrderObserver observer : OBSERVERS) {
            observer.onOrderStateChanged(event);
        }
    }
}
