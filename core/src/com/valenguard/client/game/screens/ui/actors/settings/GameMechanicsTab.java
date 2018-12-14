package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;

public class GameMechanicsTab extends Tab {

    private String title;
    private Table content;

    GameMechanicsTab() {
        super(false, false);
        title = " Game Mechanics ";
        build();
    }

    private void build() {
        content = new VisTable(true);

        // Show FPS
        final VisCheckBox fpsCheckBox = new VisCheckBox("");
        fpsCheckBox.setChecked(false);

        content.row();
        content.add(new VisLabel("Show FPS")).padRight(3);
        content.add(fpsCheckBox).left();

        fpsCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
                    Valenguard.getInstance().getStageHandler().getFpsTable().setVisible(fpsCheckBox.isChecked());
                } else {
                    event.cancel();
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
