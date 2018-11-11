package com.valenguard.client.game.screens.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ConnectionMessageUI implements AbstractUI {

    private static final boolean DEBUG_STAGE = false;
    private final String connectionMessage;
    private final Color color;

    public ConnectionMessageUI(String connectionMessage, Color color) {
        this.connectionMessage = connectionMessage;
        this.color = color;
    }

    @Override
    public void build(UiManager uiManager) {
        Table messageTable = new Table();
        messageTable.setFillParent(true);
//        messageTable.setBackground("translucent-pane");
//        messageTable.pad(10.0f);
        messageTable.setDebug(DEBUG_STAGE);
        messageTable.setColor(Color.RED);
        uiManager.stage.addActor(messageTable);

        // create login widgets
        Label infoMessageLabel = new Label(connectionMessage, uiManager.skin);
        infoMessageLabel.setColor(color);

        messageTable.add(infoMessageLabel);
    }
}
