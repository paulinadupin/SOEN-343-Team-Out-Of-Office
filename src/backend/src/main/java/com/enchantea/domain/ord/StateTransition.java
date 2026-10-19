package com.enchantea.domain.ord;

import java.time.Instant;

/** a recorded change to an order's state. fromStatus is null for the initial placed event. */
public record StateTransition(
        String eventId,
        OrderStatus fromStatus,
        OrderStatus toStatus,
        Instant timestamp) { }
