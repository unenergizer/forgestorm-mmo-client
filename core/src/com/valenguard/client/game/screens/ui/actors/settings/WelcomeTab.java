package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;

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

        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEM_TEXTURES);
        TextureRegion textureRegion = textureAtlas.findRegion("quest_098");
        VisImage visImage = new VisImage(new TextureRegionDrawable(textureRegion));

        VisTable labelTable = new VisTable(true);
        VisLabel visTextArea1 = new VisLabel("Click the tabs above to change your games settings.");
        VisLabel visTextArea2 = new VisLabel("These sections will be expanded soon to include more options.");

        labelTable.add(visTextArea1).left();
        labelTable.row();
        labelTable.add(visTextArea2).left();

        content.add(visImage).expand().width(16 * 3).height(16 * 3).padRight(3);
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
