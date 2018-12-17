package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class FPSTable extends VisTable implements Buildable {

    private VisLabel fpsLabel;

    @Override
    public Actor build() {
        fpsLabel = new VisLabel("FPS: 999");
        add(fpsLabel);

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(10, Gdx.graphics.getHeight() - getHeight() - 10);
            }
        });

        pack();
        setPosition(10, Gdx.graphics.getHeight() - getHeight() - 10);
        setVisible(false);
        return this;
    }

    public void refresh() {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }
}
