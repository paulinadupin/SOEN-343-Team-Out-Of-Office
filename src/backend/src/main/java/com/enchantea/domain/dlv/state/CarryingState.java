package com.enchantea.domain.dlv.state;

import com.enchantea.domain.dlv.CourierState;
import com.enchantea.domain.dlv.CourierStatus;

public class CarryingState extends AbstractCourierState{

    @Override
    public CourierStatus status() { return CourierStatus.CARRYING; }
    
    @Override
    public CourierState confirmDelivery() { return new IdleState(); }

}
