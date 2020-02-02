package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.Player;

import lombok.Setter;

public class CharacterInspectionWindow extends HideableVisWindow implements Buildable {

    private final CharacterInspectionWindow characterInspectionWindow;
    @Setter
    private Player playerToInspect;

    public CharacterInspectionWindow() {
        super("");
        characterInspectionWindow = this;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {


        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), 10);
            }
        });

        pack();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), 10);
        setVisible(false);
        return this;
    }

    public void inspectCharacter(int[] itemIds) {
        getTitleLabel().setText("Inspecting " + playerToInspect.getEntityName());


        if (!isVisible()) ActorUtil.fadeInWindow(characterInspectionWindow);

        pack();
    }
}
