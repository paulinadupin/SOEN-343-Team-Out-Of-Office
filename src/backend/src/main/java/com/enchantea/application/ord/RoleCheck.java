package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.reg.AccountType;
import java.util.EnumMap;
import java.util.Map;

public class RoleCheck extends CommandCheck {
    private static final Map<CommandType, AccountType> ALLOWED_ROLE = new EnumMap<>(Map.of(
            CommandType.PLACE_ORDER, AccountType.CUSTOMER,
            CommandType.CANCEL_ORDER_CUSTOMER, AccountType.CUSTOMER,
            CommandType.ACCEPT_ORDER, AccountType.RESTAURANT,
            CommandType.REJECT_ORDER, AccountType.RESTAURANT,
            CommandType.PREPARE_ORDER, AccountType.RESTAURANT,
            CommandType.MARK_ORDER_READY, AccountType.RESTAURANT,
            CommandType.PICK_UP_ORDER, AccountType.COURIER,
            CommandType.DELIVER_ORDER, AccountType.COURIER,
            CommandType.CANCEL_ORDER_OPERATOR, AccountType.OPERATOR));

    @Override
    protected void verify(CommandType type, CurrentUser user, Object command) {
        AccountType allowed = ALLOWED_ROLE.get(type);
        if (allowed != null && user.role() != allowed) {
            throw new CommandRejectedException(user.role() + " cannot " + type);
        }
    }
}
