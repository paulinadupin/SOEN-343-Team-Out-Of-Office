package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.reg.AccountType;
import java.util.EnumMap;
import java.util.Map;

public class RoleCheck extends CommandCheck {
    //Map.of only takes 10 pairs, so use ofEntries
    private static final Map<CommandType, AccountType> ALLOWED_ROLE = new EnumMap<>(Map.ofEntries(
            Map.entry(CommandType.PLACE_ORDER, AccountType.CUSTOMER),
            Map.entry(CommandType.CANCEL_ORDER_CUSTOMER, AccountType.CUSTOMER),
            Map.entry(CommandType.VIEW_ORDER_SUMMARY, AccountType.CUSTOMER),
            Map.entry(CommandType.ACCEPT_ORDER, AccountType.RESTAURANT),
            Map.entry(CommandType.REJECT_ORDER, AccountType.RESTAURANT),
            Map.entry(CommandType.PREPARE_ORDER, AccountType.RESTAURANT),
            Map.entry(CommandType.MARK_ORDER_READY, AccountType.RESTAURANT),
            Map.entry(CommandType.PICK_UP_ORDER, AccountType.COURIER),
            Map.entry(CommandType.DELIVER_ORDER, AccountType.COURIER),
            Map.entry(CommandType.CANCEL_ORDER_OPERATOR, AccountType.OPERATOR),
            Map.entry(CommandType.REFUND_ORDER, AccountType.OPERATOR)));

    @Override
    protected void verify(CommandType type, CurrentUser user, Object command) {
        AccountType allowed = ALLOWED_ROLE.get(type);
        if (allowed != null && user.role() != allowed) {
            throw new CommandRejectedException(user.role() + " cannot " + type);
        }
    }
}