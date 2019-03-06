package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class FadeWindow extends HideableVisWindow implements Buildable {

    public FadeWindow() {
        super("");
    }

    @Override
    public Actor build() {
        pad(0);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Setting true here so the black screen is shown while the GameScreen is being setup.
        setVisible(true);

        VisLabel visLabel = new VisLabel("Loading Area...");
        visLabel.setAlignment(Alignment.TOP.getAlignment());
        add(visLabel).expand().fill().padTop(5);


        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });


        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        });
        return this;
    }
}