package com.valenguard.client.game.world.item.trade;

import com.valenguard.client.game.GameQuitReset;

import lombok.Data;

@Data
public class TradeManager implements GameQuitReset {
    public Integer tradeUUID = null;

    @Override
    public void reset() {
        tradeUUID = null;
    }
}
