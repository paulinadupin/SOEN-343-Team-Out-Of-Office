package com.enchantea.ui.dlv;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;

import com.enchantea.application.reg.AccountFacade;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.User;

@RestController
@RequestMapping("/api/courier")
public class CourierController{
    private final AccountFacade ACCOUNT_FACADE;

    public CourierController(AccountFacade ACCOUNT_FACADE){
        this.ACCOUNT_FACADE = ACCOUNT_FACADE;
    }

    @GetMapping("/assignment")
    public CourierView state(HttpServletRequest servletRequest){
        return toView(currentCourier(servletRequest));
    }

    @PostMapping("/online")
    @Transactional
    public CourierView goOnline(HttpServletRequest servletRequest){
        Courier courier = currentCourier(servletRequest);
        courier.goOnline();
        return toView(courier);
    }

    @PostMapping("/offline")
    @Transactional
    public CourierView goOffline(HttpServletRequest servletRequest){
        Courier courier = currentCourier(servletRequest);
        courier.goOffline();
        return toView(courier);
    }

    @PostMapping("/offer/accept")
    @Transactional
    public CourierView acceptOffer(HttpServletRequest servletRequest){
        Courier courier = currentCourier(servletRequest);
        courier.acceptOffer();
        return toView(courier);
    }

    @PostMapping("/offer/decline")
    @Transactional
    public CourierView declineOffer(HttpServletRequest servletRequest){
        Courier courier = currentCourier(servletRequest);
        courier.declineOffer();
        return toView(courier);
    }

    @PostMapping("/pickup")
    @Transactional
    public CourierView confirmPickup(HttpServletRequest servletRequest){
        Courier courier = currentCourier(servletRequest);
        courier.confirmPickup();
        return toView(courier);
    }

    @PostMapping("/delivery")
    @Transactional
    public CourierView confirmDelivery(HttpServletRequest servletRequest){
        Courier courier = currentCourier(servletRequest);
        courier.confirmDelivery();
        return toView(courier);
    }

    private Courier currentCourier(HttpServletRequest servletRequest){
        User user = ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession(false));
        if(!(user instanceof Courier courier)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Courier account required");
        }
        return courier;
    }

    private static CourierView toView(Courier courier){
        return new CourierView(courier.getStatus());
    }

    public record CourierView(CourierStatus courierStatus){}
}

