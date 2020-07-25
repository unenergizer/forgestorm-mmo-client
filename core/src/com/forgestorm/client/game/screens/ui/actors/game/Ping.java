package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class Ping extends VisTable implements Buildable {

    private final VisLabel pingLabel = new VisLabel("000ms");

    public void setPing(long ping) {
        if (!isVisible()) return;
        String color = "[GREEN]";
        if (ping > 100) color = "[RED]";
        if (ping > 50) color = "[YELLOW]";

        pingLabel.setText(color + ping + "ms");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        pad(5f);

        add(pingLabel).align(Alignment.LEFT.getAlignment());

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        pack();
        findPosition();
        setVisible(false);
        return this;
    }

    private void findPosition() {
        setPosition(Gdx.graphics.getWidth() - StageHandler.WINDOW_PAD_X - getWidth(), Gdx.graphics.getHeight() - StageHandler.WINDOW_PAD_Y - getHeight());
    }
}
