package com.forgestorm.client.game.world.item.trade;

import com.forgestorm.client.game.GameQuitReset;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;

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
