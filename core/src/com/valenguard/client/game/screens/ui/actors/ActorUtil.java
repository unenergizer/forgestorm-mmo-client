package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.StageHandler;

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

    public static boolean fadeOutWindow(HideableVisWindow hideableVisWindow, float time) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (visibleStatus) hideableVisWindow.fadeOut(time);
        return visibleStatus;
    }

    public static boolean fadeInWindow(HideableVisWindow hideableVisWindow) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (!visibleStatus) hideableVisWindow.fadeIn().setVisible(true);
        hideableVisWindow.toFront();
        return visibleStatus;
    }

    public static boolean fadeInWindow(HideableVisWindow hideableVisWindow, float time) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (!visibleStatus) hideableVisWindow.fadeIn(time).setVisible(true);
        hideableVisWindow.toFront();
        return visibleStatus;
    }

    public static StageHandler getStageHandler() {
        return Valenguard.getInstance().getStageHandler();
    }

    public static Stage getStage() {
        return getStageHandler().getStage();
    }
}
