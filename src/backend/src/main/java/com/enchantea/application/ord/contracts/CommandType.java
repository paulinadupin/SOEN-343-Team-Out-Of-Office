package com.enchantea.application.ord.contracts;

public enum CommandType {
    PLACE_ORDER,
    ACCEPT_ORDER,
    REJECT_ORDER,
    CANCEL_ORDER_CUSTOMER,
    CANCEL_ORDER_OPERATOR,
    PREPARE_ORDER,
    MARK_ORDER_READY,
    PICK_UP_ORDER,
    DELIVER_ORDER,
    REFUND_ORDER
}
