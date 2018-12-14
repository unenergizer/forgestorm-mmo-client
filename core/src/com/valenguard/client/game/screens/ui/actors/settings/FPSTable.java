package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.screens.ui.actors.Buildable;

public class FPSTable extends VisTable implements Buildable {

    private VisLabel fpsLabel;

    @Override
    public Actor build() {
        fpsLabel = new VisLabel("FPS: 999");
        add(fpsLabel);

        pack();
        setPosition(10, Gdx.graphics.getHeight() - getHeight() - 10);
        setVisible(false);
        return this;
    }

    public void refresh() {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }
}
