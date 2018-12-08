package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.screens.ui.actors.Buildable;

public class VersionTable extends VisTable implements Buildable {

    @Override
    public Actor build() {
        add(new VisLabel("ClientConnection version " + ClientConstants.GAME_VERSION));

        pack();
        setPosition(10, 10);
        setVisible(false);
        return this;
    }
}
