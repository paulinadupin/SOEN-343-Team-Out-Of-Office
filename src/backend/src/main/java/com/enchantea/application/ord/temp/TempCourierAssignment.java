package com.enchantea.application.ord.temp;

import com.enchantea.application.ord.contracts.CourierAssignment;
import org.springframework.stereotype.Component;

/** TEMP no-op until the courier assignment module is connected. */
@Component
public class TempCourierAssignment implements CourierAssignment {
    @Override
    public void removeOrderAssignment(String orderId) {
        // There are no courier assignments in the TEMP implementation.
    }
}
