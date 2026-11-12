package com.enchantea.domain.dlv.state;
import com.enchantea.domain.dlv.CourierState;
import com.enchantea.domain.dlv.InvalidCourierTransitionException;

abstract class AbstractCourierState implements CourierState {

    @Override
    public CourierState goOffline() { return illegal("go offline"); }

    @Override
    public CourierState goOnline() { return illegal("go online"); }

    @Override
    public CourierState acceptOffer() { return illegal("accept order"); }

    @Override
    public CourierState declineOffer() { return illegal("decline order"); }

    @Override
    public CourierState confirmDelivery() { return illegal("confirm delivery"); }

    @Override
    public CourierState confirmPickup() { return illegal("confirm pickup"); }

    @Override
    public CourierState operatorCancelAssignment() { return illegal("cancel assignment"); }


    private CourierState illegal(String action) {
        throw new InvalidCourierTransitionException(status(), action);
    }
    
}
