package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.window;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;

public class CollisionWindow extends HideableVisWindow implements Buildable {

    public CollisionWindow() {
        super("Collision Editor");
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        setVisible(false);

        return this;
    }
}
