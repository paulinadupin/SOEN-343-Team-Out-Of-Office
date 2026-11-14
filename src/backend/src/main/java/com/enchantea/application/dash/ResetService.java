package com.enchantea.application.dash;

import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.PreparationQueue;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.RestaurantStatus;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.dlv.PersistedHandoffRepository;
import com.enchantea.foundation.ord.PersistedOrderRepository;
import com.enchantea.foundation.prc.PersistedLedgerEntryRepository;
import com.enchantea.foundation.reg.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ResetService {
    private final PersistedOrderRepository ORDERS;
    private final PersistedLedgerEntryRepository LEDGER_ENTRIES;
    private final PersistedHandoffRepository HANDOFFS;
    private final UserRepository USERS;
    private final DispatchQueue DISPATCH_QUEUE;
    private final PreparationQueue PREPARATION_QUEUE;

    public ResetService(
            PersistedOrderRepository orders,
            PersistedLedgerEntryRepository ledgerEntries,
            PersistedHandoffRepository handoffs,
            UserRepository users,
            DispatchQueue dispatchQueue,
            PreparationQueue preparationQueue) {
        this.ORDERS = orders;
        this.LEDGER_ENTRIES = ledgerEntries;
        this.HANDOFFS = handoffs;
        this.USERS = users;
        this.DISPATCH_QUEUE = dispatchQueue;
        this.PREPARATION_QUEUE = preparationQueue;
    }

    @Transactional
    public void reset() {
        LEDGER_ENTRIES.deleteAll();
        HANDOFFS.deleteAll();
        ORDERS.deleteAll();

        DISPATCH_QUEUE.clear();
        PREPARATION_QUEUE.clear();

        for (User user : USERS.findAll()) {
            if (user instanceof Courier courier) {
                resetCourier(courier);
            } else if (user instanceof Restaurant restaurant) {
                restaurant.setStatus(RestaurantStatus.OPEN);
                USERS.save(restaurant);
            }
        }
    }

    private void resetCourier(Courier courier) {
        if (courier.getStatus() == CourierStatus.ASSIGNED || courier.getStatus() == CourierStatus.CARRYING) {
            courier.operatorCancelAssignment();
        }
        courier.setAssignment(null);
        if (courier.getStatus() == CourierStatus.IDLE) {
            courier.goOffline();
        }
        USERS.save(courier);
    }
}
