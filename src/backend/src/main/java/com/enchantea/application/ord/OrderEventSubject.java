package com.enchantea.application.ord;
import java.util.ArrayList;
import java.util.List;

//subject in the Observer pattern. It sends each order event to the observers that subscribed to it.
public final class OrderEventSubject {
    private final List<OrderObserver> observers = new ArrayList<>();

    //add an observer so it gets future order events.
    public void subscribe(OrderObserver observer) {
        observers.add(observer);
    }

    //remove an observer so it stops getting order events.
    public void unsubscribe(OrderObserver observer) {
        observers.remove(observer);
    }

    //send the event to every observer (for example, OrderHistoryRecorder).
    public void publish(OrderEvent event) {
        for (OrderObserver observer : observers) {
            observer.onOrderStateChanged(event);
        }
    }
}
