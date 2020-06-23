package com.forgestorm.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.StageHandler;

import lombok.Getter;

public class GameMechanicsTab extends Tab {

    private final StageHandler stageHandler;
    @Getter
    private final VisCheckBox fpsCheckBox = new VisCheckBox("");
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

        content.row();
        content.add(new VisLabel("Show FPS")).padRight(3);
        content.add(fpsCheckBox).left();

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
