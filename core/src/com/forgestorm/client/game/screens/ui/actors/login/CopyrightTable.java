package com.forgestorm.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;

public class CopyrightTable extends VisTable implements Buildable {

    @Override
    public Actor build(final StageHandler stageHandler) {
        add(new VisLabel("Copyright © 2017-2020 RetroMMO. All Rights Reserved."));

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2) + 10, 10);
            }
        });

        pack();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2) + 10, 10);
        setVisible(false);
        return this;
    }
}
