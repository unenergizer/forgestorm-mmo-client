package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.kotcrab.vis.ui.FocusManager;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.KeyBinds;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.WindowManager;
import com.valenguard.client.game.screens.ui.actors.constant.WindowModes;

class PreStageEvent implements InputProcessor {

    private final StageHandler stageHandler;
    private boolean userInterfaceDebug = false;

    PreStageEvent(StageHandler stageHandler) {
        this.stageHandler = stageHandler;
    }

    @Override
    public boolean keyDown(int keycode) {
        /*
         * Toggle Chat Box Focus
         */
        if (keycode == KeyBinds.CHAT_BOX_FOCUS && Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
            if (!stageHandler.getChatWindow().isChatToggled()) {
                FocusManager.switchFocus(stageHandler.getStage(), stageHandler.getChatWindow().getMessageInput());
                stageHandler.getStage().setKeyboardFocus(stageHandler.getChatWindow().getMessageInput());
                stageHandler.getChatWindow().setChatToggled(true);
                stageHandler.getChatWindow().getMessageInput().setText("");
                return true;
            }
        }

        /*
         * Open Player Bag
         */
        if (keycode == KeyBinds.INVENTORY_WINDOW) {
            if (!stageHandler.getChatWindow().isChatToggled()
                    && !stageHandler.getMainSettingsWindow().isVisible()
                    && !stageHandler.getEscapeWindow().isVisible()
                    && Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
                if (!stageHandler.getBagWindow().isVisible()) {
                    stageHandler.getBagWindow().fadeIn().setVisible(true);
                    FocusManager.switchFocus(stageHandler.getStage(), stageHandler.getBagWindow());
                } else {
                    stageHandler.getBagWindow().fadeOut();
                }
                return true;
            }
        }

        /*
         * Open Equipment Window
         */
        if (keycode == KeyBinds.EQUIPMENT_WINDOW) {
            if (!stageHandler.getChatWindow().isChatToggled()
                    && !stageHandler.getMainSettingsWindow().isVisible()
                    && !stageHandler.getEscapeWindow().isVisible()
                    && Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
                if (!stageHandler.getEquipmentWindow().isVisible()) {
                    stageHandler.getEquipmentWindow().fadeIn().setVisible(true);
                    FocusManager.switchFocus(stageHandler.getStage(), stageHandler.getEquipmentWindow());
                } else {
                    stageHandler.getEquipmentWindow().fadeOut();
                }
                return true;
            }
        }

        /*
         * Toggle Game Debug
         */
        if (keycode == KeyBinds.GAME_DEBUG) {
            stageHandler.getDebugTable().setVisible(!stageHandler.getDebugTable().isVisible());
            if (stageHandler.getFpsTable().isVisible()) {
                stageHandler.getFpsTable().setVisible(false);
                stageHandler.getMainSettingsWindow().getGameMechanicsTab().getFpsCheckBox().setChecked(false);
            }
            return true;
        }

        /*
         * Toggle Full Screen
         */
        if (keycode == KeyBinds.FULLSCREEN) {
            WindowManager windowManager = Valenguard.getInstance().getWindowManager();
            if (windowManager.getCurrentWindowMode() != WindowModes.WINDOW) {
//                windowManager.setWindowMode(WindowModes.WINDOW);
                stageHandler.getMainSettingsWindow().getGraphicsTab().setWindowMode(WindowModes.WINDOW);
            } else {
//                windowManager.setWindowMode(WindowModes.FULL_SCREEN_NO_WINDOW);
                stageHandler.getMainSettingsWindow().getGraphicsTab().setWindowMode(WindowModes.FULL_SCREEN_NO_WINDOW);
            }
            return true;
        }

        /*
         * Toggle UI Debug
         */
        if (keycode == KeyBinds.SCENE2D_DEBUG) {
            userInterfaceDebug = !userInterfaceDebug;
            for (Actor actor : stageHandler.getStage().getActors()) {
                if (actor instanceof Group) {
                    Group group = (Group) actor;
                    group.setDebug(userInterfaceDebug, true);
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
