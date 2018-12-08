package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.kotcrab.vis.ui.FocusManager;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;

public class PostStageEvent implements InputProcessor {

    private final StageHandler stageHandler;

    PostStageEvent(StageHandler stageHandler) {
        this.stageHandler = stageHandler;
    }

    @Override
    public boolean keyDown(int keycode) {
        /*
         * Close Open Windows or Open Escape Menu
         */
        if (keycode == Input.Keys.ESCAPE) {

            /*
             * Tidy up and close any known stubborn windows.
             * TODO: Abstract this out into an Exitable Window interface we can call automatically
             */
            boolean openWindowFound = false;
            if (stageHandler.getEscapeWindow().isVisible()) {
                stageHandler.getEscapeWindow().fadeOut();
                openWindowFound = true;
            }
            if (stageHandler.getHelpWindow().isVisible()) {
                stageHandler.getHelpWindow().fadeOut();
                openWindowFound = true;
            }
            if (stageHandler.getCreditsWindow().isVisible()) {
                stageHandler.getCreditsWindow().fadeOut();
                openWindowFound = true;
            }
            if (stageHandler.getInventoryWindow().isVisible()) {
                stageHandler.getInventoryWindow().fadeOut();
                openWindowFound = true;
            }
            if (stageHandler.getMainSettingsWindow().isVisible()) {
                stageHandler.getMainSettingsWindow().fadeOut();
                openWindowFound = true;
            }
            if (openWindowFound) return true; // break here, do not open escape menu!

            /*
             * Finally... Open the Escape menu!
             */
            if (!stageHandler.getEscapeWindow().isVisible()
                    && Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
                stageHandler.getEscapeWindow().fadeIn().setVisible(true);
                FocusManager.switchFocus(stageHandler.getStage(), stageHandler.getEscapeWindow());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
