package com.valenguard.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.movement.KeyboardMovement;
import com.valenguard.client.game.screens.UserInterfaceType;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class Keyboard implements InputProcessor {

    @Getter
    private KeyboardMovement keyboardMovement = new KeyboardMovement();

    public boolean keyDown(int keycode) {
        if (Valenguard.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) return false;

        /*
         * Movement Debug
         */
        if (keycode == Input.Keys.F4) {
            ClientConstants.MONITOR_MOVEMENT_CHECKS = !ClientConstants.MONITOR_MOVEMENT_CHECKS;
            println(getClass(), "Toggled walking debug: " + ClientConstants.MONITOR_MOVEMENT_CHECKS, true);
        }

        /*
         * Character Movement
         */
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
