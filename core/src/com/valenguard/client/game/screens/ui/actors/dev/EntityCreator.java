package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class EntityCreator extends HideableVisWindow implements Buildable {

    public EntityCreator() {
        super("EntityCreator");
    }

    @Override
    public Actor build() {

        VisLabel visLabel = new VisLabel("TODO :)");

        add(visLabel);

        addCloseButton();
        centerWindow();
        setVisible(false);
        return this;
    }
}
