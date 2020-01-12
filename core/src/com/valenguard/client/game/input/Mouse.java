package com.valenguard.client.game.input;

import com.badlogic.gdx.InputProcessor;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.UserInterfaceType;

public class Mouse implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
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
        if (Valenguard.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) return false;
        if (!Valenguard.gameScreen.isGameFocused()) return false;
        Valenguard.getInstance().getMouseManager().mouseClick(screenX, screenY, button);
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
        Valenguard.getInstance().getMouseManager().mouseMove(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        Valenguard.gameScreen.getCamera().scrollZoomLevel(amount * ClientConstants.ZOOM_CHANGE);
        return false;
    }
}
