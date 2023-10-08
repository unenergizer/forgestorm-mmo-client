package com.forgestorm.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.movement.KeyboardMovement;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class Keyboard implements InputProcessor {

    @Getter
    private final KeyboardMovement keyboardMovement = new KeyboardMovement();

    public boolean keyDown(int keycode) {
        if (ClientMain.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) return false;
        if (ActorUtil.getStageHandler().getChatWindow().isChatToggled()) return false;

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
        if (ClientMain.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) return false;
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

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
