package com.valenguard.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.entities.animations.HumanAnimation;
import com.valenguard.client.game.movement.KeyboardMovement;
import com.valenguard.client.network.packet.out.AppearanceChange;
import com.valenguard.client.util.Log;

import lombok.Getter;

public class Keyboard implements InputProcessor {


    @Getter
    private KeyboardMovement keyboardMovement = new KeyboardMovement();


    public boolean keyDown(int keycode) {

        /*
         * Movement Debug
         */
        if (keycode == Input.Keys.F4) {
            ClientConstants.MONITOR_MOVEMENT_CHECKS = !ClientConstants.MONITOR_MOVEMENT_CHECKS;
            Log.println(getClass(), "Toggled walking debug: " + ClientConstants.MONITOR_MOVEMENT_CHECKS, true);
        }

        /*
         * Change appearance
         */
        boolean changed = false;
        if (keycode == Input.Keys.NUMPAD_4) {
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            HumanAnimation humanAnimation = (HumanAnimation) playerClient.getEntityAnimation();
            if (humanAnimation.getHeadId() - 1 < 0)
                humanAnimation.setHeadId((short) (ClientConstants.HUMAN_MAX_HEADS + 1));
            humanAnimation.loadAllVarArgs(GameAtlas.ENTITY_CHARACTER, (short) (humanAnimation.getHeadId() - 1), humanAnimation.getBodyId());
            changed = true;
        } else if (keycode == Input.Keys.NUMPAD_5) {
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            HumanAnimation humanAnimation = (HumanAnimation) playerClient.getEntityAnimation();
            if (humanAnimation.getHeadId() + 1 > ClientConstants.HUMAN_MAX_HEADS)
                humanAnimation.setHeadId((short) -1);
            humanAnimation.loadAllVarArgs(GameAtlas.ENTITY_CHARACTER, (short) (humanAnimation.getHeadId() + 1), humanAnimation.getBodyId());
            changed = true;
        } else if (keycode == Input.Keys.NUMPAD_1) {
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            HumanAnimation humanAnimation = (HumanAnimation) playerClient.getEntityAnimation();
            if (humanAnimation.getBodyId() - 1 < 0)
                humanAnimation.setBodyId((short) (ClientConstants.HUMAN_MAX_BODIES + 1));
            humanAnimation.loadAllVarArgs(GameAtlas.ENTITY_CHARACTER, humanAnimation.getHeadId(), (short) (humanAnimation.getBodyId() - 1));
            changed = true;
        } else if (keycode == Input.Keys.NUMPAD_2) {
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            HumanAnimation humanAnimation = (HumanAnimation) playerClient.getEntityAnimation();
            if (humanAnimation.getBodyId() + 1 > ClientConstants.HUMAN_MAX_BODIES)
                humanAnimation.setBodyId((short) -1);
            humanAnimation.loadAllVarArgs(GameAtlas.ENTITY_CHARACTER, humanAnimation.getHeadId(), (short) (humanAnimation.getBodyId() + 1));
            changed = true;
        }
        if (changed) {
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            HumanAnimation humanAnimation = (HumanAnimation) playerClient.getEntityAnimation();
            new AppearanceChange(humanAnimation.getHeadId(), humanAnimation.getBodyId()).sendPacket();
            return true;
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
