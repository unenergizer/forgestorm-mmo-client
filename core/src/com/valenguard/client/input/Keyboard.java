package com.valenguard.client.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.valenguard.client.Valenguard;
import com.valenguard.client.screens.stage.GameScreenDebugText;

public class Keyboard implements InputProcessor {

    private boolean showDebug;

    public boolean keyDown(int keycode) {
        // Screen debug toggle
        if (keycode == Input.Keys.F3) {
            if (showDebug) {
                showDebug = false;
                Valenguard.getInstance().getUiManager().show(new GameScreenDebugText());
            } else {
                showDebug = true;
                Valenguard.getInstance().getUiManager().show(null);
            }
            return false;
        }

        Valenguard.getInstance().getMovementManager().keyDown(keycode);
        return false;
    }

    public boolean keyUp(int keycode) {
        Valenguard.getInstance().getMovementManager().keyUp(keycode);
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        return false;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }
}
