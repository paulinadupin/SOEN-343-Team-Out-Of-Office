package com.enchantea.ui.prc;

import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.prc.OrderHistoryEntry;
import com.enchantea.application.prc.OrderHistoryService;
import com.enchantea.application.reg.AccountFacade;
import com.enchantea.domain.prc.LedgerEntry;
import com.enchantea.domain.prc.LedgerEntryType;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;
import java.time.Instant;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderHistoryController {
    private final OrderHistoryService HISTORY_SERVICE;
    private final AccountFacade ACCOUNT_FACADE;

    public OrderHistoryController(OrderHistoryService HISTORY_SERVICE, AccountFacade ACCOUNT_FACADE) {
        this.HISTORY_SERVICE = HISTORY_SERVICE;
        this.ACCOUNT_FACADE = ACCOUNT_FACADE;
    }

    @GetMapping("/history")
    public List<OrderHistoryView> history(HttpServletRequest servletRequest) {
        return HISTORY_SERVICE.historyFor(currentUser(servletRequest)).stream()
                .map(OrderHistoryController::toView)
                .toList();
    }

    private CurrentUser currentUser(HttpServletRequest servletRequest) {
        User user = ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession(false));
        Long restaurantId = user instanceof Restaurant ? user.getUserId() : null;
        return new CurrentUser(user.getUserId(), user.getAccountType(), restaurantId);
    }

    private static OrderHistoryView toView(OrderHistoryEntry entry) {
        List<LedgerEntryView> ledgerEntries = entry.ledgerEntries().stream()
                .map(OrderHistoryController::toLedgerView)
                .toList();
        return new OrderHistoryView(
                OrderSummaryController.toView(entry.summary()),
                ledgerEntries,
                entry.chargedTotal(),
                entry.refundedTotal(),
                entry.netTotal());
    }

    private static LedgerEntryView toLedgerView(LedgerEntry entry) {
        return new LedgerEntryView(entry.entryId(), entry.type(), entry.amount(), entry.createdAt());
    }

    public record LedgerEntryView(
            String entryId,
            LedgerEntryType type,
            double amount,
            Instant createdAt) { }

    public record OrderHistoryView(
            OrderSummaryController.OrderSummaryView summary,
            List<LedgerEntryView> ledgerEntries,
            double chargedTotal,
            double refundedTotal,
            double netTotal) { }
}