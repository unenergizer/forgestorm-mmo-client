package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.screens.ui.ImageBuilder;

public class WelcomeTab extends Tab {

    private String title;
    private Table content;

    WelcomeTab() {
        super(false, true);
        title = " Welcome! ";
        build();
    }

    private void build() {
        content = new VisTable(true);
        VisImage visImage = new ImageBuilder(GameAtlas.ITEMS, "skill_165", 16 * 3).buildVisImage();

        VisTable labelTable = new VisTable(true);
        VisLabel visTextArea1 = new VisLabel("Click the tabs above to change your games settings.");
        VisLabel visTextArea2 = new VisLabel("These sections will be expanded soon to include more options.");

        labelTable.add(visTextArea1).left();
        labelTable.row();
        labelTable.add(visTextArea2).left();

        content.add(visImage).padRight(3);
        content.add(labelTable);
        content.center();
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
