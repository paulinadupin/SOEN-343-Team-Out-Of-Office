package com.enchantea.domain.dlv.state;

import com.enchantea.domain.dlv.CourierState;
import com.enchantea.domain.reg.CourierStatus;

public class AssignedState extends AbstractCourierState {

    @Override
    public CourierStatus status() { return CourierStatus.ASSIGNED; }

    @Override
    public CourierState confirmPickup() { return new CarryingState(); }
    
}
