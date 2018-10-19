package com.valenguard.client.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.valenguard.client.Valenguard;
import com.valenguard.client.constants.ClientConstants;
import com.valenguard.client.constants.Direction;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.movement.MovementManager;
import com.valenguard.client.screens.stage.GameScreenDebugText;

public class Controller implements InputProcessor {

    private boolean showDebug;
    private Vector3 clickLocation = new Vector3();

    public boolean keyDown(int keycode) {
        //  System.out.println("keyDOWN: " + keycode);
        if (keycode == Input.Keys.F3) {
            if (showDebug) {
                showDebug = false;
                Valenguard.getInstance().getUiManager().show(new GameScreenDebugText());
            } else {
                showDebug = true;
                Valenguard.getInstance().getUiManager().show(null);
            }
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        //  System.out.println("keyUP: " + keycode);



        return false;
    }

    public boolean keyTyped(char character) {
        MovementManager movementManager = Valenguard.getInstance().getMovementManager();
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        switch (character) {
            case 'w':
                movementManager.playerMove(playerClient, 0, 1, Direction.UP);
                break;
            case 's':
                movementManager.playerMove(playerClient, 0, -1, Direction.DOWN);
                break;
            case 'a':
                movementManager.playerMove(playerClient, -1, 0, Direction.LEFT);
                break;
            case 'd':
                movementManager.playerMove(playerClient, 1, 0, Direction.RIGHT);
                break;
        }

        return false;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        Vector3 worldCoordinates = Valenguard.gameScreen.getCamera().unproject(clickLocation.set(x, y, 0));
        Valenguard.getInstance().getMovementManager().mouseClick(
                (int) worldCoordinates.x / ClientConstants.TILE_SIZE,
                (int) worldCoordinates.y / ClientConstants.TILE_SIZE);
        return false;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        // System.out.println("touchUP- X: " + x + ", Y: " + y + ", POINTER: " + pointer + ", BUTTON: " + button);
        return false;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        // System.out.println("touchDRAGGED- X: " + x + ", Y: " + y + ", POINTER: " + pointer);
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        Vector3 worldCoordinates = Valenguard.gameScreen.getCamera().unproject(clickLocation.set(x, y, 0));
        Valenguard.getInstance().getMovementManager().mouseMove(
                (int) worldCoordinates.x / ClientConstants.TILE_SIZE,
                (int) worldCoordinates.y / ClientConstants.TILE_SIZE);
        return false;
    }

    public boolean scrolled(int amount) {
        if(amount == -1) {
            Valenguard.gameScreen.getCamera().changeZoomLevel(-ClientConstants.ZOOM_CHANGE);
        } else if (amount == 1) {
            Valenguard.gameScreen.getCamera().changeZoomLevel(ClientConstants.ZOOM_CHANGE);
        }
        return false;
    }
}

