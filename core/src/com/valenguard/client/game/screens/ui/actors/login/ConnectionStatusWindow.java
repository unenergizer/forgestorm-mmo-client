package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

import lombok.Getter;

@Getter
public class ConnectionStatusWindow extends HideableVisWindow implements Buildable {

    private final HideableVisWindow hideableVisWindow = new HideableVisWindow("", "dialog");
    private final VisLabel statusMessage = new VisLabel();

    public ConnectionStatusWindow() {
        super("", "chat-box");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        hideableVisWindow.pad(30);
        hideableVisWindow.add(statusMessage);
        hideableVisWindow.setMovable(false);
        hideableVisWindow.setResizable(false);
        hideableVisWindow.addListener(new InputListener() {
            /** Called when a mouse button or a finger touch goes down on the actor. If true is returned, this listener will receive all
             * touchDragged and touchUp events, even those not over this actor, until touchUp is received. Also when true is returned, the
             * event is {@link Event#handle() handled}.
             * @see InputEvent */
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        setFillParent(true);
        setMovable(false);
        setResizable(false);
        setVisible(false);
        setWidth(Gdx.graphics.getWidth());
        setHeight(Gdx.graphics.getHeight());
        addListener(new InputListener() {
            /** Called when a mouse button or a finger touch goes down on the actor. If true is returned, this listener will receive all
             * touchDragged and touchUp events, even those not over this actor, until touchUp is received. Also when true is returned, the
             * event is {@link Event#handle() handled}.
             * @see InputEvent */
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        pad(0);
        add(hideableVisWindow);

        hideableVisWindow.setPosition((getWidth() / 2) - (hideableVisWindow.getWidth() / 2), (getHeight() / 2) - (hideableVisWindow.getHeight() / 2));
        pack();
        return this;
    }

    public void setStatusMessage(String statusMessage) {
        if (!isVisible()) ActorUtil.fadeInWindow(this);
        this.statusMessage.setText(statusMessage);
        pack();
    }

}
