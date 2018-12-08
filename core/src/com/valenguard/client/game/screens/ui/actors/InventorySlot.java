package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.kotcrab.vis.ui.widget.VisImage;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.screens.ui.Buildable;
import com.valenguard.client.game.screens.ui.StageHandler;

public class InventorySlot extends Stack implements Buildable {

    private final StageHandler stageHandler;

    public InventorySlot(StageHandler stageHandler) {
        this.stageHandler = stageHandler;
    }

    @Override
    public Actor build() {
        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEM_TEXTURES);
        TextureRegion textureRegion = textureAtlas.findRegion("potion_01");
        VisImage image = new VisImage(textureRegion);
        add(image);
        return this;
    }
}
