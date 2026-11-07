package com.enchantea.domain.dlv.state;

import com.enchantea.domain.dlv.CourierState;
import com.enchantea.domain.dlv.CourierStatus;

public class IdleState extends AbstractCourierState{

     @Override
    public CourierStatus status() { return CourierStatus.IDLE; }

    @Override
    public CourierState acceptOffer() { return new AssignedState(); }

    @Override
    public CourierState declineOffer() { return this;}

    @Override
    public CourierState goOffline() { return new OfflineState(); }
 
}
