package com.enchantea.domain.reg;

import com.enchantea.domain.dlv.CourierState;
import com.enchantea.domain.dlv.Assignment;
import com.enchantea.domain.dlv.state.AssignedState;
import com.enchantea.domain.dlv.state.CarryingState;
import com.enchantea.domain.dlv.state.IdleState;
import com.enchantea.domain.dlv.state.OfflineState;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("COURIER")
public class Courier extends User{
    @Enumerated(EnumType.STRING)
    @Column(name="courier_status")
    private CourierStatus status;

    @Embedded
    @AttributeOverride(name = "lat", column = @Column(name = "courier_lat"))
    @AttributeOverride(name = "lng", column = @Column(name = "courier_lng"))
    private Location position;

    @Transient
    private Assignment assignment;

    protected Courier() {
    }

    public Courier(String username, String passwordHash, CourierStatus status, Location position) {
        super(username, passwordHash);
        this.status = status;
        this.position = position;
    }

    // --- state transition methods ---

    public void goOnline(){
        CourierState state = getState();
        setState(state.goOnline());
    }

    public void goOffline(){
        CourierState state = getState();
        setState(state.goOffline());
    }

    public void acceptOffer(){
        CourierState state = getState();
        setState(state.acceptOffer());
    }

    public void declineOffer(){
        CourierState state = getState();
        setState(state.declineOffer());
    }

    public void confirmPickup(){
        CourierState state = getState();
        setState(state.confirmPickup());
    }

    public void confirmDelivery(){
        CourierState state = getState();
        setState(state.confirmDelivery());
    }

    public void operatorCancelAssignment() {
        CourierState state = getState();
        setState(state.operatorCancelAssignment());
    }

    // --- getters and setters ---

    public CourierStatus getStatus() {
        return status;
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.COURIER;
    }

    public CourierState getState(){
        return switch (status){
            case OFFLINE -> new OfflineState();
            case IDLE -> new IdleState();
            case ASSIGNED -> new AssignedState();
            case CARRYING -> new CarryingState();
        };
    }

    private void setState(CourierState state){
        this.status=state.status();
    }
}
