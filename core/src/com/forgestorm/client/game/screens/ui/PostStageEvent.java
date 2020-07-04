package com.forgestorm.client.game.screens.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.KeyBinds;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowEvent;

class PostStageEvent implements InputProcessor {

    private final StageHandler stageHandler;

    PostStageEvent(StageHandler stageHandler) {
        this.stageHandler = stageHandler;
    }

    @Override
    public boolean keyDown(int keycode) {
        /*
         * Close Open Windows or Open Escape Menu
         */
        if (keycode == KeyBinds.ESCAPE_ACTION) {

            // Close any current open windows on first Esc press (if no windows open, we continue).
            ForceCloseWindowEvent forceCloseEvent = new ForceCloseWindowEvent();
            for (Actor actor : stageHandler.getStage().getActors()) actor.fire(forceCloseEvent);
            if (forceCloseEvent.isHandled()) return true; // break here, do not open escape menu!

            //Finally... Open the Escape menu!
            if (ClientMain.getInstance().getUserInterfaceType() == UserInterfaceType.GAME) {
                if (!stageHandler.getEscapeWindow().isVisible()) {
                    ActorUtil.fadeInWindow(stageHandler.getEscapeWindow());
                }
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