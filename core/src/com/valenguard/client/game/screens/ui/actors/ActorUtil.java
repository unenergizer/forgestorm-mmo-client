package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorUtil {

    private ActorUtil() {
    }

    private static final Vector2 localCords = new Vector2();

    public static Vector2 getStageLocation(Actor actor) {
        return actor.localToStageCoordinates(localCords.set(0, 0));
    }

    public static boolean fadeOutWindow(HideableVisWindow hideableVisWindow) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (visibleStatus) hideableVisWindow.fadeOut();
        return visibleStatus;
    }

    public static boolean fadeInWindow(HideableVisWindow hideableVisWindow) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (!visibleStatus) hideableVisWindow.fadeIn().setVisible(true);
        return visibleStatus;
    }
}
