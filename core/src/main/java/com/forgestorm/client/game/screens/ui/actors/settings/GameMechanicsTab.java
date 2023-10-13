package com.forgestorm.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import lombok.Getter;

public class GameMechanicsTab extends Tab {

    private final StageHandler stageHandler;
    @Getter
    private final VisCheckBox fpsCheckBox = new VisCheckBox("");
    @Getter
    private final VisCheckBox playerNameVisibleCheckBox = new VisCheckBox("");
    @Getter
    private final VisCheckBox playerHealthBarVisibleCheckBox = new VisCheckBox("");
    @Getter
    private final VisCheckBox otherPlayerNameVisibleCheckBox = new VisCheckBox("");
    @Getter
    private final VisCheckBox otherPlayerHealthBarVisibleCheckBox = new VisCheckBox("");
    @Getter
    private final VisCheckBox entityNameVisibleCheckBox = new VisCheckBox("");
    @Getter
    private final VisCheckBox entityHealthBarVisibleCheckBox = new VisCheckBox("");
    private final String title;
    private Table content;

    GameMechanicsTab(StageHandler stageHandler) {
        super(false, false);
        this.stageHandler = stageHandler;
        title = " Game Mechanics ";
        build(stageHandler.getClientMain());
    }

    private void build(ClientMain clientMain) {
        content = new VisTable(true);

        // Show FPS
        fpsCheckBox.setChecked(false);

        VisTable fpsTable = new VisTable();
        fpsTable.add(new VisLabel("Show FPS")).padRight(3);
        fpsTable.add(fpsCheckBox).left();
        content.add(fpsTable).row();

        fpsCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientMain.getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
                if (clientMain.getUserInterfaceType() == UserInterfaceType.GAME) {
                    stageHandler.getFpsTable().setVisible(fpsCheckBox.isChecked());
                    if (fpsCheckBox.isChecked())
                        stageHandler.getDebugTable().setVisible(false);
                } else {
                    event.cancel();
                    Dialogs.showOKDialog(stageHandler.getStage(), "Error!", "Option can only be set in-game.");
                }
                event.handle();
            }
        });

        // Show players username over head
        playerNameVisibleCheckBox.setChecked(true);

        VisTable usernameVisibleTable = new VisTable();
        usernameVisibleTable.add(new VisLabel("Show My Username")).padRight(3);
        usernameVisibleTable.add(playerNameVisibleCheckBox).left();
        content.add(usernameVisibleTable).row();

        playerNameVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientMain.getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
            }
        });

        // Show players health bar over head
        playerHealthBarVisibleCheckBox.setChecked(true);

        VisTable healthBarVisibleTable = new VisTable();
        healthBarVisibleTable.add(new VisLabel("Show My Health bar")).padRight(3);
        healthBarVisibleTable.add(playerHealthBarVisibleCheckBox).left();
        content.add(healthBarVisibleTable).row();

        playerHealthBarVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientMain.getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
            }
        });

        // Show entity username over head
        entityNameVisibleCheckBox.setChecked(true);

        VisTable entityNameVisibleTable = new VisTable();
        entityNameVisibleTable.add(new VisLabel("Show Monster Usernames")).padRight(3);
        entityNameVisibleTable.add(entityNameVisibleCheckBox).left();
        content.add(entityNameVisibleTable).row();

        entityNameVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientMain.getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
            }
        });

        // Show entity health bar over head
        entityHealthBarVisibleCheckBox.setChecked(true);

        VisTable entityHealthBarVisibleTable = new VisTable();
        entityHealthBarVisibleTable.add(new VisLabel("Show Monster Health bars")).padRight(3);
        entityHealthBarVisibleTable.add(entityHealthBarVisibleCheckBox).left();
        content.add(entityHealthBarVisibleTable).row();

        entityHealthBarVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientMain.getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
            }
        });


        // Show players username over head
        otherPlayerNameVisibleCheckBox.setChecked(true);

        VisTable otherPlayerNameVisibleTable = new VisTable();
        otherPlayerNameVisibleTable.add(new VisLabel("Show Other Player Usernames")).padRight(3);
        otherPlayerNameVisibleTable.add(otherPlayerNameVisibleCheckBox).left();
        content.add(otherPlayerNameVisibleTable).row();

        otherPlayerNameVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientMain.getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
            }
        });

        // Show players health bar over head
        otherPlayerHealthBarVisibleCheckBox.setChecked(true);

        VisTable otherPlayerHealthBarVisibleTable = new VisTable();
        otherPlayerHealthBarVisibleTable.add(new VisLabel("Show Other Player Health bars")).padRight(3);
        otherPlayerHealthBarVisibleTable.add(otherPlayerHealthBarVisibleCheckBox).left();
        content.add(otherPlayerHealthBarVisibleTable).row();

        otherPlayerHealthBarVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientMain.getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
            }
        });
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public Table getContentTable() {
        return content;
    }
}
