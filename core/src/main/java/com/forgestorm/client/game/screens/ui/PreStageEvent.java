package com.forgestorm.client.game.screens.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.KeyBinds;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.WindowManager;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.constant.WindowModes;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.HotBar;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.NPC;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;

class PreStageEvent implements InputProcessor {

    private final StageHandler stageHandler;
    private boolean userInterfaceDebug = false;

    PreStageEvent(StageHandler stageHandler) {
        this.stageHandler = stageHandler;
    }

    /**
     * Checks to make sure dev tools are closed. For non admin players,
     * dev tool UI elements are never created so these elements will return
     * null.
     *
     * @return True if elements are not null and are visible, false otherwise.
     */
    private boolean devToolsClosed() {
        if (stageHandler.getEntityEditor() == null) {
            return true;
        } else {
            return !stageHandler.getEntityEditor().isVisible();
        }
    }

    /**
     * Keys combinations that can be pressed only on the GameScreen.
     *
     * @param keycode The key being pressed.
     * @return True if key is handled, false otherwise.
     */
    private boolean gameScreenOnlyKeys(int keycode) {
        if (stageHandler.getChatWindow().isChatToggled()) return false;
        /*
         * Toggle Chat Box Focus
         */
        if (keycode == KeyBinds.CHAT_BOX_FOCUS) {
            if (!stageHandler.getChatWindow().isChatToggled()) {
                stageHandler.getChatWindow().toggleChatWindowActive(true);
                return true;
            }
        }

        // Fade out the chat window
        ChatWindow chatWindow = ActorUtil.getStageHandler().getChatWindow();
        if (!chatWindow.isWindowFaded()) chatWindow.toggleChatWindowInactive(true, true);

        /*
         * Tool bar keys
         */
        HotBar hotBar = stageHandler.getHotBar();
        if (keycode == KeyBinds.ACTION_1) hotBar.hotBarInteract((byte) 0);
        if (keycode == KeyBinds.ACTION_2) hotBar.hotBarInteract((byte) 1);
        if (keycode == KeyBinds.ACTION_3) hotBar.hotBarInteract((byte) 2);
        if (keycode == KeyBinds.ACTION_4) hotBar.hotBarInteract((byte) 3);
        if (keycode == KeyBinds.ACTION_5) hotBar.hotBarInteract((byte) 4);
        if (keycode == KeyBinds.ACTION_6) hotBar.hotBarInteract((byte) 5);
        if (keycode == KeyBinds.ACTION_7) hotBar.hotBarInteract((byte) 6);
        if (keycode == KeyBinds.ACTION_8) hotBar.hotBarInteract((byte) 7);
        if (keycode == KeyBinds.ACTION_9) hotBar.hotBarInteract((byte) 8);
        if (keycode == KeyBinds.ACTION_10) hotBar.hotBarInteract((byte) 9);

        /*
         * Open Player Profile
         * TODO: REMOVE ME. THIS IS FOR DEBUGGING ONLY!
         */
        if (keycode == Input.Keys.MINUS) {
            stageHandler.getPlayerProfileWindow().requestPlayerProfile(EntityManager.getInstance().getPlayerClient());
        }

        /*
         * Make sure these windows are closed...
         */
        if (!stageHandler.getChatWindow().isChatToggled()
                && !stageHandler.getMainSettingsWindow().isVisible()
                && !stageHandler.getEscapeWindow().isVisible()
                && devToolsClosed()) {

            /*
             * Open Player Bag
             */
            if (keycode == KeyBinds.INVENTORY_WINDOW) {
                if (!stageHandler.getBagWindow().isVisible()) {
                    stageHandler.getBagWindow().openWindow();
                } else {
                    stageHandler.getBagWindow().closeWindow();
                }
                return true;
            }

            /*
             * Open Equipment Window
             */
            if (keycode == KeyBinds.EQUIPMENT_WINDOW) {
                if (!stageHandler.getEquipmentWindow().isVisible()) {
                    stageHandler.getEquipmentWindow().openWindow();
                } else {
                    ActorUtil.fadeOutWindow(stageHandler.getEquipmentWindow());
                }
                return true;
            }

            /*
             * Interacting with environment
             */
            if (keycode == KeyBinds.INTERACT) {
                EntityManager entityManager = EntityManager.getInstance();

                PlayerClient playerClient = entityManager.getPlayerClient();
                Location possibleNpcTile = new Location(playerClient.getCurrentMapLocation()).add(playerClient.getFacingDirection());

                NPC npc = new NPC();
                npc.chat();

                for (AiEntity aiEntity : entityManager.getAiEntityList().values()) {
                    if (!(aiEntity instanceof NPC)) continue;

                    if (aiEntity.getFutureMapLocation().equals(possibleNpcTile)) {
                        ((NPC) aiEntity).chat();
                        break;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Keys combinations that can be pressed on any screen.
     *
     * @param keycode The key being pressed.
     * @return True if key is handled, false otherwise.
     */
    private boolean anyScreenKeys(int keycode) {
        /*
         * Toggle Game Debug
         */
        if (keycode == KeyBinds.GAME_DEBUG) {
            stageHandler.getDebugTable().setVisible(!stageHandler.getDebugTable().isVisible());
            if (stageHandler.getFpsTable() != null && stageHandler.getFpsTable().isVisible()) {
                stageHandler.getFpsTable().setVisible(false);
                stageHandler.getMainSettingsWindow().getGameMechanicsTab().getFpsCheckBox().setChecked(false);
            }
            return true;
        }

        /*
         * Toggle Full Screen
         */
        if (keycode == KeyBinds.FULLSCREEN) {
            WindowManager windowManager = ClientMain.getInstance().getWindowManager();
            if (windowManager.getCurrentWindowMode() != WindowModes.WINDOW) {
                stageHandler.getMainSettingsWindow().getGraphicsTab().setWindowMode(WindowModes.WINDOW);
            } else {
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
    public boolean keyDown(int keycode) {
        boolean bool;
        bool = anyScreenKeys(keycode);
        if (ClientMain.getInstance().getUserInterfaceType() == UserInterfaceType.GAME) {
            bool = gameScreenOnlyKeys(keycode);
        }
        return bool;
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
        if (ClientMain.getInstance().getUserInterfaceType() == UserInterfaceType.GAME) {
            // Fade out the chat window
            ChatWindow chatWindow = ActorUtil.getStageHandler().getChatWindow();
            if (!chatWindow.isWindowFaded()) chatWindow.toggleChatWindowInactive(true, true);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
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
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
