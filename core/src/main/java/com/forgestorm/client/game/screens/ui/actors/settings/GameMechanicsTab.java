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
    private final VisCheckBox usernameVisibleCheckBox = new VisCheckBox("");
    @Getter
    private final VisCheckBox healthBarVisibleCheckBox = new VisCheckBox("");
    private final String title;
    private Table content;

    GameMechanicsTab(StageHandler stageHandler) {
        super(false, false);
        this.stageHandler = stageHandler;
        title = " Game Mechanics ";
        build();
    }

    private void build() {
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
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
                if (ClientMain.getInstance().getUserInterfaceType() == UserInterfaceType.GAME) {
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
        usernameVisibleCheckBox.setChecked(true);

        VisTable usernameVisibleTable = new VisTable();
        usernameVisibleTable.add(new VisLabel("Show My Username")).padRight(3);
        usernameVisibleTable.add(usernameVisibleCheckBox).left();
        content.add(usernameVisibleTable).row();

        usernameVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
            }
        });

        // Show players health bar over head
        healthBarVisibleCheckBox.setChecked(true);

        VisTable healthBarVisibleTable = new VisTable();
        healthBarVisibleTable.add(new VisLabel("Show My Health bar")).padRight(3);
        healthBarVisibleTable.add(healthBarVisibleCheckBox).left();
        content.add(healthBarVisibleTable).row();

        healthBarVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
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
