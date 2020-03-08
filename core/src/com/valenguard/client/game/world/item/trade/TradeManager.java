package com.valenguard.client.game.world.item.trade;

import com.valenguard.client.game.GameQuitReset;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;

import lombok.Data;

@Data
public class TradeManager implements GameQuitReset {

    public Integer tradeUUID = null;

    public boolean isTradeActive() {
        return ActorUtil.getStageHandler().getTradeWindow().isVisible();
    }

    @Override
    public void gameQuitReset() {
        tradeUUID = null;
    }
}
