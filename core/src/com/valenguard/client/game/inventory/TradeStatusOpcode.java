package com.valenguard.client.game.inventory;

public enum TradeStatusOpcode {

    // Stage 1: Init trade
    TRADE_REQUEST_INIT_SENDER,
    TRADE_REQUEST_INIT_TARGET,

    // Stage 2: Wait for TargetPlayer response or time out
    TRADE_REQUEST_TARGET_ACCEPT,
    TRADE_REQUEST_TARGET_DECLINE,
    TRADE_REQUEST_SERVER_TIMED_OUT,

    // Stage 3: Trade started -> adding/removing items from trade window
    TRADE_ITEM_ADD,
    TRADE_ITEM_REMOVE,

    // Stage 4: First Trade Confirm (items are in window, do trade or cancel)
    TRADE_OFFER_CONFIRM,
    TRADE_OFFER_UNCONFIRM,

    // Stage 5: Final trade confirm
    TRADE_OFFER_COMPLETE,

    // Generic trade cancel
    TRADE_CANCELED;

    public static TradeStatusOpcode getTradeStatusOpcode(byte entityTypeByte) {
        for (TradeStatusOpcode tradeStatusOpcode : TradeStatusOpcode.values()) {
            if ((byte) tradeStatusOpcode.ordinal() == entityTypeByte) {
                return tradeStatusOpcode;
            }
        }
        return null;
    }

    public byte getTradeOpcodeByte() {
        return (byte) this.ordinal();
    }
}
