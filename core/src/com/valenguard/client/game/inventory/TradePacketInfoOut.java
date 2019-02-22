package com.valenguard.client.game.inventory;

import lombok.Getter;

@Getter
public class TradePacketInfoOut {

    private TradeStatusOpcode tradeStatusOpcode;
    private int tradeUUID;
    private short targetEntityUUID;
    private ItemStack itemStack;

    public TradePacketInfoOut(TradeStatusOpcode tradeStatusOpcode, short targetEntityUUID) {
        this.tradeStatusOpcode = tradeStatusOpcode;
        this.targetEntityUUID = targetEntityUUID;
    }

    public TradePacketInfoOut(TradeStatusOpcode tradeStatusOpcode, int tradeUUID) {
        this.tradeStatusOpcode = tradeStatusOpcode;
        this.tradeUUID = tradeUUID;
    }

    public TradePacketInfoOut(TradeStatusOpcode tradeStatusOpcode, int tradeUUID, ItemStack itemStack) {
        this.tradeStatusOpcode = tradeStatusOpcode;
        this.tradeUUID = tradeUUID;
        this.itemStack = itemStack;
    }
}
