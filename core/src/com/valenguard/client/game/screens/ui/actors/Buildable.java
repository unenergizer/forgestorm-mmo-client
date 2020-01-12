package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.valenguard.client.game.screens.ui.StageHandler;

public interface Buildable {
    Actor build(final StageHandler stageHandler);
}
