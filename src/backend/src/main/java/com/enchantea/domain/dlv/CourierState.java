package com.enchantea.domain.dlv;

//all actions related to courier's status
public interface CourierState {
    CourierStatus status();
    CourierState goOnline();
    CourierState goOffline();
    CourierState acceptOffer();
    CourierState declineOffer(); // TODO : check if this is useful
    CourierState confirmPickup();
    CourierState confirmDelivery();
}
