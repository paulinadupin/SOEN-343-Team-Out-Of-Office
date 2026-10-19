package com.enchantea.application.ord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventConfiguration {
    
    //creates one event subject for the app to publish order events through
    @Bean // @Bean tells Spring to create and manage the object returned by each method
    public OrderEventSubject orderEventSubject() {
        return new OrderEventSubject();
    }

    // creates the history recorder and subscribes it to order events
    @Bean
    public OrderHistoryRecorder orderHistoryRecorder(OrderEventSubject subject) {
        OrderHistoryRecorder recorder = new OrderHistoryRecorder();
        subject.subscribe(recorder);
        return recorder;
    }
}
