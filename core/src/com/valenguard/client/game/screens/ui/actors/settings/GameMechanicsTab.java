package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;

import lombok.Getter;

public class GameMechanicsTab extends Tab {

    @Getter
    private final VisCheckBox fpsCheckBox = new VisCheckBox("");
    private final String title;
    private Table content;

    GameMechanicsTab() {
        super(false, false);
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
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(GameMechanicsTab.class, (short) 0);
                if (Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
                    ActorUtil.getStageHandler().getFpsTable().setVisible(fpsCheckBox.isChecked());
                    if (fpsCheckBox.isChecked())
                        ActorUtil.getStageHandler().getDebugTable().setVisible(false);
                } else {
                    event.cancel();
                    Dialogs.showOKDialog(ActorUtil.getStage(), "Error!", "Option can only be set in-game.");
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
