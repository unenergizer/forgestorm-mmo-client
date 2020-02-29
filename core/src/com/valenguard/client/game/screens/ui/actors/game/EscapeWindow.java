package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.character.CharacterLogout;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.network.game.packet.out.CharacterLogoutPacketOut;

public class EscapeWindow extends HideableVisWindow implements Buildable {

    private EscapeWindow escapeWindow;

    public EscapeWindow() {
        super("");
    }

    private final VisTextButton help = new VisTextButton("Help");
    private final VisTextButton credits = new VisTextButton("Credits");
    private final VisTextButton settings = new VisTextButton("Settings");
    private final VisTextButton logout = new VisTextButton("Logout");
    private final VisTextButton exitGame = new VisTextButton("Exit Game");
    private final VisTextButton returnToGame = new VisTextButton("Return to Game");

    @Override
    public Actor build(final StageHandler stageHandler) {
        escapeWindow = this;
        setMovable(false);
        TableUtils.setSpacingDefaults(this);
        VisTable table = new VisTable();


        returnToGame.setColor(Color.GREEN);

        pad(3);

        table.add(help).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(credits).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(settings).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(logout).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(exitGame).fill().pad(0, 0, 6, 0);
        table.row();
        table.add(returnToGame).fill();
        table.row();

        add(table);

        pack();
        centerWindow();

        setVisible(false);

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        help.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EscapeWindow.class, (short) 0);
                ActorUtil.fadeOutWindow(escapeWindow);
                ActorUtil.fadeInWindow(stageHandler.getHelpWindow());
                return true;
            }
        });

        credits.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EscapeWindow.class, (short) 0);
                ActorUtil.fadeOutWindow(escapeWindow);
                ActorUtil.fadeInWindow(stageHandler.getCreditsWindow());
                return true;
            }
        });

        settings.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EscapeWindow.class, (short) 0);
                ActorUtil.fadeOutWindow(escapeWindow);
                ActorUtil.fadeInWindow(stageHandler.getMainSettingsWindow());
                return true;
            }
        });

        logout.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EscapeWindow.class, (short) 0);
                new CharacterLogoutPacketOut(CharacterLogout.LOGOUT_CHARACTER).sendPacket();

                // Waiting on server to do its thing... Fade in the big black window..
                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);
                disableButtons(true);

                // Reset Inventories
                stageHandler.getBagWindow().resetItemSlotContainer();
                stageHandler.getBankWindow().resetItemSlotContainer();
                stageHandler.getEquipmentWindow().resetItemSlotContainer();
                return true;
            }
        });

        exitGame.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EscapeWindow.class, (short) 0);
                new CharacterLogoutPacketOut(CharacterLogout.LOGOUT_SERVER).sendPacket();
                Valenguard.connectionManager.disconnect();
                Gdx.app.exit();
                return true;
            }
        });

        returnToGame.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EscapeWindow.class, (short) 0);
                ActorUtil.fadeOutWindow(escapeWindow);
                return true;
            }
        });


        stopWindowClickThrough();

        return this;
    }

    public void disableButtons(boolean setDisabled) {
        help.setDisabled(setDisabled);
        credits.setDisabled(setDisabled);
        settings.setDisabled(setDisabled);
        logout.setDisabled(setDisabled);
        exitGame.setDisabled(setDisabled);
        returnToGame.setDisabled(setDisabled);
    }

}
