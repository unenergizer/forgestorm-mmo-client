package com.valenguard.client.game.input;

import com.badlogic.gdx.InputProcessor;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.movement.KeyboardMovement;
import com.valenguard.client.game.screens.stage.GameScreenDebugText;

import lombok.Getter;

public class Keyboard implements InputProcessor {

    @Getter
    private KeyboardMovement keyboardMovement = new KeyboardMovement();
    private boolean showDebug = true;

    public boolean keyDown(int keycode) {
        // Screen debug toggle
        if (keycode == KeyBinds.PRINT_DEBUG) {
            if (showDebug) {
                showDebug = false;
                Valenguard.getInstance().getUiManager().show(new GameScreenDebugText());
            } else {
                showDebug = true;
                Valenguard.getInstance().getUiManager().show(null);
            }
            return false;
        }

        keyboardMovement.keyDown(keycode);
        return false;
    }

    public boolean keyUp(int keycode) {
        keyboardMovement.keyUp(keycode);
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
