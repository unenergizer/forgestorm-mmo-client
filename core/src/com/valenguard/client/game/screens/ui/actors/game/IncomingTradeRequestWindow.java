package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.TradePacketInfoOut;
import com.valenguard.client.game.inventory.TradeStatus;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.network.packet.out.PlayerTradePacketOut;

public class IncomingTradeRequestWindow extends HideableVisWindow implements Buildable {

    private TextButton accept;
    private TextButton cancel;


    public IncomingTradeRequestWindow() {
        super("Incoming Trade Request");
    }

    @Override
    public Actor build() {

        addCloseButton();

        accept = new TextButton("Accept", VisUI.getSkin());
        cancel = new TextButton("Cancel", VisUI.getSkin());

        add(accept).expand().fill();
        add(cancel).expand().fill();

        accept.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                closeOnEscape();
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatus.TRADE_REQUEST_ACCEPT, Valenguard.getInstance().getTradeManager().getTradeUUID())).sendPacket();
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                closeOnEscape();
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatus.TRADE_REQUEST_DECLINE, Valenguard.getInstance().getTradeManager().getTradeUUID())).sendPacket();
            }
        });


        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
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
        Viewport viewport = Valenguard.getInstance().getStageHandler().getStage().getViewport();
        setPosition(viewport.getScreenWidth() / 2, viewport.getScreenHeight() / 2);
        return this;
    }
}
