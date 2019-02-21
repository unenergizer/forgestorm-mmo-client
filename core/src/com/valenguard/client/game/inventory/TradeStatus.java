package com.valenguard.client.game.inventory;

public enum TradeStatus {

    // Stage 1: Init trade
    TRADE_REQUEST_PLAYER_SENDER,
    TRADE_REQUEST_PLAYER_TARGET,

    // Stage 2: Wait for TargetPlayer response
    TRADE_REQUEST_ACCEPT,
    TRADE_REQUEST_DECLINE,
    TRADE_REQUEST_TIMED_OUT,

    // Stage 3: Trade started -> adding/removing items from trade window
    TRADE_ITEM_ADD,
    TRADE_ITEM_REMOVE,

    // Stage 4: Trade finished (items are in window, do trade or cancel)
    TRADE_OFFER_ACCEPT,
    TRADE_OFFER_DECLINE;

    public static TradeStatus getTradeStatusOpcode(byte entityTypeByte) {
        for (TradeStatus tradeStatus : TradeStatus.values()) {
            if ((byte) tradeStatus.ordinal() == entityTypeByte) {
                return tradeStatus;
            }
        }
        return null;
    }

    public byte getTradeOpcodeByte() {
        return (byte) this.ordinal();
    }
}
