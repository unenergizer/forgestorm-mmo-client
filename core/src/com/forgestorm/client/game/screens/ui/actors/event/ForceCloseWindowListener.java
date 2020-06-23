package com.forgestorm.client.game.screens.ui.actors.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;

public abstract class ForceCloseWindowListener implements EventListener {

    @Override
    public boolean handle(Event event) {
        if (!(event instanceof ForceCloseWindowEvent)) return false;
        if (!(event.getListenerActor() instanceof HideableVisWindow)) return false;
        if (ActorUtil.fadeOutWindow((HideableVisWindow) event.getListenerActor())) {
            handleClose();
            return true;
        }
        return false;
    }

    public abstract void handleClose();

}

