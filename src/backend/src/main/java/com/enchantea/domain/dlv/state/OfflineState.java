package com.enchantea.domain.dlv.state;

import com.enchantea.domain.dlv.CourierState;
import com.enchantea.domain.dlv.CourierStatus;

public class OfflineState extends AbstractCourierState{

    @Override
    public CourierStatus status() { return CourierStatus.OFFLINE; }
    
    @Override
    public CourierState goOnline() { return new IdleState(); }

}
