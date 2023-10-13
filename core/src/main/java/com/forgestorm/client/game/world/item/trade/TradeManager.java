package com.forgestorm.client.game.world.item.trade;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.GameQuitReset;
import lombok.Data;

@Data
public class TradeManager implements GameQuitReset {

    private final ClientMain clientMain;

    public Integer tradeUUID = null;

    public TradeManager(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    public boolean isTradeActive() {
        return clientMain.getStageHandler().getTradeWindow().isVisible();
    }

    @Override
    public void gameQuitReset() {
        tradeUUID = null;
    }
}
