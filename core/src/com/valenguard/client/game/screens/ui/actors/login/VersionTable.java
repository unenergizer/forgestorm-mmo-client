package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class VersionTable extends VisTable implements Buildable {

    @Override
    public Actor build(final StageHandler stageHandler) {
        add(new VisLabel("Client Version " + ClientConstants.GAME_VERSION));

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(10, 10);
            }
        });

        pack();
        setPosition(10, 10);
        setVisible(false);
        return this;
    }
}
