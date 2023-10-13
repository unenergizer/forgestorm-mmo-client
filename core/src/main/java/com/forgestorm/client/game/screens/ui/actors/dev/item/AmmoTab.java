package com.forgestorm.client.game.screens.ui.actors.dev.item;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

class AmmoTab extends Tab {

    private final String title;
    private Table content;

    AmmoTab(ClientMain clientMain) {
        super(false, false);
        title = " Ammo ";
        build(clientMain);
    }

    private void build(ClientMain clientMain) {
        content = new VisTable(true);
        VisImage visImage = new ImageBuilder(clientMain, GameAtlas.ITEMS, "skill_165", 16 * 3).buildVisImage();

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
