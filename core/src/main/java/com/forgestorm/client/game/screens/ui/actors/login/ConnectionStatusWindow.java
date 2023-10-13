package com.forgestorm.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.kotcrab.vis.ui.widget.VisLabel;
import lombok.Getter;

@Getter
public class ConnectionStatusWindow extends HideableVisWindow implements Buildable {

    private StageHandler stageHandler;
    private VisLabel statusMessage;

    public ConnectionStatusWindow(ClientMain clientMain) {
        super(clientMain, "Connection Status:", "dialog");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        statusMessage = new VisLabel();

        add(statusMessage).pad(30);

        addCloseButton();
        stopWindowClickThrough();
        findPosition();
        pack();
        return this;
    }

    private void findPosition() {
        setPosition(
                (float) (Gdx.graphics.getWidth() / 2) - (getWidth() / 2),
                stageHandler.getLoginTable().getY() - stageHandler.getLoginTable().getHeight() - getHeight() - 10
        );
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage.setText(statusMessage);
        setVisible(true);
        pack();
        findPosition();
    }

}
