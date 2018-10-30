package com.valenguard.client.input;

import com.badlogic.gdx.InputProcessor;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;

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
        return Valenguard.getInstance().getMouseManager().mouseClick(screenX, screenY);
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
        return Valenguard.getInstance().getMouseManager().mouseMove(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        Valenguard.gameScreen.getCamera().changeZoomLevel(amount * ClientConstants.ZOOM_CHANGE);
        return false;
    }
}
