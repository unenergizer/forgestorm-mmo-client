package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;

public class NPCBodyPart {

    private final NPCEditor npcEditor;
    private final VisTable visTable;
    private final String texture;
    private final int maxTextures;
    private final int width;
    private final int height;
    private final ImageData imageData;
    private final boolean useCheckBox;

    @Getter
    private VisCheckBox visCheckBox;
    private VisTable imageArea;
    private VisLabel textureId;
    private int currentTexture = 0;

    NPCBodyPart(NPCEditor npcEditor, VisTable visTable, String texture, int maxTextures, int width, int height, ImageData imageData, boolean useCheckBox) {
        this.npcEditor = npcEditor;
        this.visTable = visTable;
        this.texture = texture;
        this.maxTextures = maxTextures;
        this.width = width;
        this.height = height;
        this.imageData = imageData;
        this.useCheckBox = useCheckBox;
    }

    void setData(int currentTexture, boolean enabled) {
        this.currentTexture = currentTexture;
        visCheckBox.setChecked(enabled);
        update();
    }

    void setData(int currentTexture) {
        this.currentTexture = currentTexture;
        update();
    }

    private void update() {
        imageArea.clearChildren();
        imageArea.setWidth(width);
        imageArea.setHeight(height);
        imageArea.add(new ImageBuilder(GameAtlas.ENTITY_CHARACTER).setWidth(width).setHeight(height).setRegionName(texture + "_down_" + currentTexture).buildVisImage());

        if (currentTexture > 9) {
            textureId.setText("ID: " + currentTexture + "/" + maxTextures);
        } else {
            textureId.setText("ID: 0" + currentTexture + "/" + maxTextures);
        }
    }

    public void build() {
        final VisTable innerTable = new VisTable();
        visCheckBox = new VisCheckBox("Enable: ");
        final VisTextButton previous = new VisTextButton("<");
        imageArea = new VisTable();
        imageArea.setWidth(width);
        imageArea.setHeight(height);
        imageArea.add(new ImageBuilder(GameAtlas.ENTITY_CHARACTER).setWidth(width).setHeight(height).setRegionName(texture + "_down_" + currentTexture).buildVisImage());
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

        visCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (visCheckBox.isChecked()) {
                    previous.setDisabled(false);
                    next.setDisabled(false);
                    imageData.setUse(true);
                } else {
                    previous.setDisabled(true);
                    next.setDisabled(true);
                    imageData.setUse(false);
                }
                npcEditor.getAppearancePanel().characterPreview();
            }
        });

        previous.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentTexture == 0) {
                    currentTexture = maxTextures;
                } else {
                    currentTexture = currentTexture - 1;
                }
                imageArea.clearChildren();
                imageArea.add(new ImageBuilder(GameAtlas.ENTITY_CHARACTER).setWidth(width).setHeight(height).setRegionName(texture + "_down_" + currentTexture).buildVisImage());
                imageData.setData(currentTexture);
                if (currentTexture > 9) {
                    textureId.setText("ID: " + currentTexture + "/" + maxTextures);
                } else {
                    textureId.setText("ID: 0" + currentTexture + "/" + maxTextures);
                }
                visTable.pack();
                npcEditor.getAppearancePanel().characterPreview();
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
                imageArea.add(new ImageBuilder(GameAtlas.ENTITY_CHARACTER).setWidth(width).setHeight(height).setRegionName(texture + "_down_" + currentTexture).buildVisImage());
                imageData.setData(currentTexture);
                if (currentTexture > 9) {
                    textureId.setText("ID: " + currentTexture + "/" + maxTextures);
                } else {
                    textureId.setText("ID: 0" + currentTexture + "/" + maxTextures);
                }
                visTable.pack();
                npcEditor.getAppearancePanel().characterPreview();
            }
        });

        if (useCheckBox) innerTable.add(visCheckBox);
        innerTable.add(previous).pad(1);
        innerTable.add(imageArea).pad(1);
        innerTable.add(next).pad(1);
        innerTable.add(textureId).pad(1);
        visTable.add(innerTable).row();
        visTable.pack();
    }

}
