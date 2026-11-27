package com.enchantea.ui.promo;

import com.enchantea.application.ord.OrderCommands;
import com.enchantea.application.promo.PromoService;
import com.enchantea.application.reg.AccountFacade;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class PromoController {
    private final PromoService PROMO_SERVICE;
    private final AccountFacade ACCOUNT_FACADE;

    public PromoController(PromoService promoService, AccountFacade accountFacade) {
        this.PROMO_SERVICE = promoService;
        this.ACCOUNT_FACADE = accountFacade;
    }

    @PostMapping("/promo")
    public PromoResponse preview(@RequestBody PromoRequest request, HttpServletRequest servletRequest) {
        ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession(false));
        double discount = PROMO_SERVICE.previewDiscount(request.code(), request.restaurantId(), request.items());
        return new PromoResponse(discount);
    }

    public record PromoRequest(Long restaurantId, String code, List<OrderCommands.RequestedLine> items) { }

    public record PromoResponse(double discount) { }
}
