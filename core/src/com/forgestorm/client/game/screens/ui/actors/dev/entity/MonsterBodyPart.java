package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.io.type.GameAtlas;

public class MonsterBodyPart {

    private final MonsterTab monsterTab;
    private final VisTable visTable;
    private final int maxTextures;
    private final int width;
    private final int height;
    private final ImageData imageData;
    private final boolean useCheckBox;

    private VisTable imageArea;
    private VisLabel textureId;
    private int currentTexture = 0;

    MonsterBodyPart(MonsterTab monsterTab, VisTable visTable, int maxTextures, int width, int height, ImageData imageData, boolean useCheckBox) {
        this.monsterTab = monsterTab;
        this.visTable = visTable;
        this.maxTextures = maxTextures;
        this.width = width;
        this.height = height;
        this.imageData = imageData;
        this.useCheckBox = useCheckBox;
    }

    void setData(int currentTexture) {
        this.currentTexture = currentTexture;
        update();
    }

    private void update() {
        imageArea.clearChildren();
        imageArea.setWidth(width);
        imageArea.setHeight(height);
        imageArea.add(new ImageBuilder(GameAtlas.ENTITY_MONSTER).setWidth(width).setHeight(height).setRegionName("monster_down_" + currentTexture).buildVisImage());

        if (currentTexture > 9) {
            textureId.setText("ID: " + currentTexture + "/" + maxTextures);
        } else {
            textureId.setText("ID: 0" + currentTexture + "/" + maxTextures);
        }
    }

    public void build() {
        final VisTable innerTable = new VisTable();
        final VisTextButton previous = new VisTextButton("<");
        imageArea = new VisTable();
        imageArea.setWidth(width);
        imageArea.setHeight(height);
        imageArea.add(new ImageBuilder(GameAtlas.ENTITY_MONSTER).setWidth(width).setHeight(height).setRegionName("monster_down_" + currentTexture).buildVisImage());
        final VisTextButton next = new VisTextButton(">");
        textureId = new VisLabel("");
        if (currentTexture > 9) {
            textureId.setText("ID: " + currentTexture + "/" + maxTextures);
        } else {
            textureId.setText("ID: 0" + currentTexture + "/" + maxTextures);
        }

        if (useCheckBox) {
            previous.setDisabled(true);
            next.setDisabled(true);
            imageData.setUse(false);
        }

        previous.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentTexture == 0) {
                    currentTexture = maxTextures;
                } else {
                    currentTexture = currentTexture - 1;
                }
                imageArea.clearChildren();
                imageArea.add(new ImageBuilder(GameAtlas.ENTITY_MONSTER).setWidth(width).setHeight(height).setRegionName("monster_down_" + currentTexture).buildVisImage());
                imageData.setData(currentTexture);
                if (currentTexture > 9) {
                    textureId.setText("ID: " + currentTexture + "/" + maxTextures);
                } else {
                    textureId.setText("ID: 0" + currentTexture + "/" + maxTextures);
                }
                visTable.pack();
                monsterTab.getAppearancePanel().characterPreview();
            }
        });

        next.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentTexture == maxTextures) {
                    currentTexture = 0;
                } else {
                    currentTexture = currentTexture + 1;
                }
                imageArea.clearChildren();
                imageArea.add(new ImageBuilder(GameAtlas.ENTITY_MONSTER).setWidth(width).setHeight(height).setRegionName("monster_down_" + currentTexture).buildVisImage());
                imageData.setData(currentTexture);
                if (currentTexture > 9) {
                    textureId.setText("ID: " + currentTexture + "/" + maxTextures);
                } else {
                    textureId.setText("ID: 0" + currentTexture + "/" + maxTextures);
                }
                visTable.pack();
                monsterTab.getAppearancePanel().characterPreview();
            }
        });

        innerTable.add(previous).pad(1);
        innerTable.add(imageArea).pad(1);
        innerTable.add(next).pad(1);
        innerTable.add(textureId).pad(1);
        visTable.add(innerTable).row();
        visTable.pack();
    }

}
