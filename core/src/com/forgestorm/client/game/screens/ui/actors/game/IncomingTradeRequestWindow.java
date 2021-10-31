package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.item.trade.TradePacketInfoOut;
import com.forgestorm.client.network.game.packet.out.PlayerTradePacketOut;
import com.forgestorm.shared.game.world.item.trade.TradeStatusOpcode;
import com.kotcrab.vis.ui.VisUI;

public class IncomingTradeRequestWindow extends HideableVisWindow implements Buildable {

    private IncomingTradeRequestWindow incomingTradeRequestWindow;

    public IncomingTradeRequestWindow() {
        super("Incoming Trade Request");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        incomingTradeRequestWindow = this;

        TextButton accept = new TextButton("Accept", VisUI.getSkin());
        TextButton cancel = new TextButton("Cancel", VisUI.getSkin());

        add(accept).expand().fill();
        add(cancel).expand().fill();

        accept.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(IncomingTradeRequestWindow.class, (short) 0);
                ActorUtil.fadeOutWindow(incomingTradeRequestWindow);
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_TARGET_ACCEPT, ClientMain.getInstance().getTradeManager().getTradeUUID())).sendPacket();
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(IncomingTradeRequestWindow.class, (short) 0);
                ActorUtil.fadeOutWindow(incomingTradeRequestWindow);
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_TARGET_DECLINE, ClientMain.getInstance().getTradeManager().getTradeUUID())).sendPacket();
            }
        });


        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_CANCELED, ClientMain.getInstance().getTradeManager().getTradeUUID())).sendPacket();
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        pack();
        setVisible(false);
        Viewport viewport = stageHandler.getStage().getViewport();
        setPosition(viewport.getScreenWidth() / 2, viewport.getScreenHeight() / 2);
        return this;
    }
}
