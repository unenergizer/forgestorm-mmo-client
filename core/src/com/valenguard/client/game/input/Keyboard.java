package com.valenguard.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.entities.animations.HumanAnimation;
import com.valenguard.client.game.movement.KeyboardMovement;
import com.valenguard.client.game.screens.stage.UiManager;
import com.valenguard.client.game.screens.stage.game.ChatBox;
import com.valenguard.client.game.screens.stage.game.GameScreenDebugText;
import com.valenguard.client.network.packet.out.AppearanceChange;
import com.valenguard.client.util.Log;

import lombok.Getter;

public class Keyboard implements InputProcessor {

    private UiManager uiManager = Valenguard.getInstance().getUiManager();

    @Getter
    private KeyboardMovement keyboardMovement = new KeyboardMovement();
    private boolean showDebug = true;

    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.F4) {
            ClientConstants.MINITOR_MOVEMENT_BUG = !ClientConstants.MINITOR_MOVEMENT_BUG;
            Log.println(getClass(), "Toggled walking debug: " + ClientConstants.MINITOR_MOVEMENT_BUG, true);
        }

        // change appearance
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

        // Screen debug toggle
        if (keycode == KeyBinds.PRINT_DEBUG) {
            if (showDebug) {
                showDebug = false;
                if (!uiManager.exist("debug"))
                    uiManager.addUi("debug", new GameScreenDebugText(), true);
                uiManager.show("debug");
            } else {
                showDebug = true;
                uiManager.hide("debug");
            }
            return true;
        }

        // Chat box and movement crap
        ChatBox chatBox = (ChatBox) uiManager.getAbstractUI("chatbox");
        if (keycode == KeyBinds.CHAT_BOX_FOCUS) {
            if (!chatBox.isPreventInput()) {
                chatBox.setPreventInput(true);
                uiManager.getStage().setKeyboardFocus(chatBox.getChatField());
            } else {
                chatBox.setPreventInput(false);
            }
        }

        Actor focusedKeyboard = uiManager.getStage().getKeyboardFocus();
        if (focusedKeyboard == null || !focusedKeyboard.equals(chatBox.getChatField())) {
            keyboardMovement.keyDown(keycode);
        }

        return false;
    }

    public boolean keyUp(int keycode) {

        ChatBox chatBox = (ChatBox) uiManager.getAbstractUI("chatbox");

        Actor focusedKeyboard = uiManager.getStage().getKeyboardFocus();
        if (focusedKeyboard == null || !focusedKeyboard.equals(chatBox.getChatField())) {
            keyboardMovement.keyUp(keycode);
        }

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
