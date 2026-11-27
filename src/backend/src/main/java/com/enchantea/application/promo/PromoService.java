package com.enchantea.application.promo;

import com.enchantea.application.ord.OrderCommands;
import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.promo.PromoCode;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** Validates promo codes against the configured rules and computes the discount (R-PROMO-01/02). */
@Service
public class PromoService {
    private final PromoConfigLoader CONFIG_LOADER;
    private final RestaurantQuery RESTAURANT_QUERY;

    public PromoService(PromoConfigLoader CONFIG_LOADER, RestaurantQuery RESTAURANT_QUERY) {
        this.CONFIG_LOADER = CONFIG_LOADER;
        this.RESTAURANT_QUERY = RESTAURANT_QUERY;
    }

    public double resolveDiscount(String code, double subtotal) {
        PromoCode promoCode = CONFIG_LOADER.get().stream()
                .filter(candidate -> candidate.code().equals(code))
                .findFirst()
                .orElseThrow(() -> new CommandRejectedException("Unknown promo code: " + code));

        if (promoCode.isExpired(Instant.now())) {
            throw new CommandRejectedException("This promo code has expired");
        }
        if (!promoCode.meetsMinimumOrderValue(subtotal)) {
            throw new CommandRejectedException(
                    "This order does not meet the minimum order value for this promo code");
        }

        return promoCode.discountFor(subtotal);
    }

    public double previewDiscount(String code, Long restaurantId, List<OrderCommands.RequestedLine> lines) {
        double subtotal = lines.stream()
                .mapToDouble(line -> RESTAURANT_QUERY.itemPrice(restaurantId, line.itemId()) * line.quantity())
                .sum();
        return resolveDiscount(code, subtotal);
    }
}
