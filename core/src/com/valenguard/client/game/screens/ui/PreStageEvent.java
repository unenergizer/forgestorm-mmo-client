package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.kotcrab.vis.ui.FocusManager;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.KeyBinds;
import com.valenguard.client.game.screens.UserInterfaceType;
import com.valenguard.client.game.screens.WindowManager;
import com.valenguard.client.game.screens.effects.AlphaFlashEffect;
import com.valenguard.client.game.screens.effects.BlackFlashEffect;
import com.valenguard.client.game.screens.effects.CircleDrawEffect;
import com.valenguard.client.game.screens.effects.EffectManager;
import com.valenguard.client.game.screens.effects.LineDrawEffect;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.constant.WindowModes;
import com.valenguard.client.game.screens.ui.actors.game.AbilityBar;
import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.NPC;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.maps.Location;

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
        /*
         * Toggle TEMP EFFECTS
         */
        EffectManager effectManager = Valenguard.getInstance().getEffectManager();

        if (keycode == Input.Keys.NUMPAD_1) {
            if (!stageHandler.getChatWindow().isChatToggled()) {
                effectManager.addScreenEffect(new BlackFlashEffect());
                return true;
            }
        }

        if (keycode == Input.Keys.NUMPAD_2) {
            if (!stageHandler.getChatWindow().isChatToggled()) {
                effectManager.addScreenEffect(new AlphaFlashEffect());
                return true;
            }
        }

        if (keycode == Input.Keys.NUMPAD_3) {
            if (!stageHandler.getChatWindow().isChatToggled()) {
                PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
                effectManager.addScreenEffect(new LineDrawEffect(Color.RED, playerClient.getDrawX(), playerClient.getDrawY(), 20, 200, 2));
                return true;
            }
        }

        if (keycode == Input.Keys.NUMPAD_4) {
            if (!stageHandler.getChatWindow().isChatToggled()) {
                PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
                effectManager.addScreenEffect(new CircleDrawEffect(ShapeRenderer.ShapeType.Line, Color.RED, playerClient.getDrawX(), playerClient.getDrawY(), 20, 200, 2));
                return true;
            }
        }

        /*
         * Toggle Chat Box Focus
         */
        if (keycode == KeyBinds.CHAT_BOX_FOCUS) {
            if (!stageHandler.getChatWindow().isChatToggled()) {
                FocusManager.switchFocus(stageHandler.getStage(), stageHandler.getChatWindow().getMessageInput());
                stageHandler.getStage().setKeyboardFocus(stageHandler.getChatWindow().getMessageInput());
                stageHandler.getChatWindow().setChatToggled(true);
                stageHandler.getChatWindow().getMessageInput().setText("");
                return true;
            }
        }

        /*
         * Tool bar keys
         * TODO: Figure out a cleaner way to do this...
         */
        AbilityBar abilityBar = Valenguard.getInstance().getStageHandler().getAbilityBar();
        if (keycode == KeyBinds.ACTION_1) {
            Valenguard.getInstance().getAbilityManager().toggleAbility((short) 0, abilityBar, abilityBar.getActionOne());
        }
        if (keycode == KeyBinds.ACTION_2) {
            Valenguard.getInstance().getAbilityManager().toggleAbility((short) 1, abilityBar, abilityBar.getActionTwo());
        }
        if (keycode == KeyBinds.ACTION_3) {
            Valenguard.getInstance().getAbilityManager().toggleAbility((short) 2, abilityBar, abilityBar.getActionThree());
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
                    ActorUtil.fadeInWindow(stageHandler.getBagWindow());
                    FocusManager.switchFocus(stageHandler.getStage(), stageHandler.getBagWindow());
                } else {
                    ActorUtil.fadeOutWindow(stageHandler.getBagWindow());
                }
                return true;
            }

            /*
             * Open Equipment Window
             */
            if (keycode == KeyBinds.EQUIPMENT_WINDOW) {
                if (!stageHandler.getEquipmentWindow().isVisible()) {
                    ActorUtil.fadeInWindow(stageHandler.getEquipmentWindow());
                    FocusManager.switchFocus(stageHandler.getStage(), stageHandler.getEquipmentWindow());
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
            WindowManager windowManager = Valenguard.getInstance().getWindowManager();
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
        if (Valenguard.getInstance().getUserInterfaceType() == UserInterfaceType.GAME) {
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
