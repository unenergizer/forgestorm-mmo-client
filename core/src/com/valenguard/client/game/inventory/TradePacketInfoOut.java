package com.valenguard.client.game.inventory;

import lombok.Getter;

@Getter
public class TradePacketInfoOut {

    private TradeStatus tradeStatus;
    private int tradeUUID;
    private short targetEntityUUID;

    public TradePacketInfoOut(TradeStatus tradeStatus, short targetEntityUUID) {
        this.tradeStatus = tradeStatus;
        this.targetEntityUUID = targetEntityUUID;
    }

    public TradePacketInfoOut(TradeStatus tradeStatus, int tradeUUID) {
        this.tradeStatus = tradeStatus;
        this.tradeUUID = tradeUUID;
    }
}
