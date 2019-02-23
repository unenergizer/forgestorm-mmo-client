package com.valenguard.client.game.screens.ui.actors;

public class ActorUtil {

    private ActorUtil() {
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
