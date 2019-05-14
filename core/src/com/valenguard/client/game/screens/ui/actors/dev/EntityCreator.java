package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.util.ColorList;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

public class EntityCreator extends HideableVisWindow implements Buildable {

    VisSelectBox entityType;
    private VisTextField name = new VisTextField();
    private VisTextField health = new VisTextField();
    private VisTextField damage = new VisTextField();
    private VisTextField expDrop = new VisTextField();
    private VisTextField dropTable = new VisTextField();
    private VisTextField walkSpeed = new VisTextField();
    private VisTextField probStill = new VisTextField();
    private VisTextField probWalk = new VisTextField();
    private VisTextField shopId = new VisTextField();
    private ImageData hairData = new ImageData();
    private ImageData helmData = new ImageData();
    private ImageData chestData = new ImageData();
    private ImageData pantsData = new ImageData();
    private ImageData shoesData = new ImageData();
    private VisSelectBox hairColor;
    private VisSelectBox eyeColor;
    private VisSelectBox skinColor;
    private VisSelectBox glovesColor;

    private VisTable previewTable = new VisTable();

    public EntityCreator() {
        super("EntityCreator");
    }

    @Override
    public Actor build() {

        VisTable leftPane = new VisTable();

        textField(leftPane, "Name:", name);
        entityType = new VisSelectBox();
        entityType.setItems(EntityType.values());
        listSelect(leftPane, "EntityType: ", entityType);
        textField(leftPane, "Health:", health);
        textField(leftPane, "Damage:", damage);
        textField(leftPane, "ExpDrop:", expDrop);
        textField(leftPane, "DropTable:", dropTable);
        textField(leftPane, "Walk Speed:", walkSpeed);
        textField(leftPane, "Probability Still:", probStill);
        textField(leftPane, "Probability Walk:", probWalk);
        textField(leftPane, "Shop ID:", shopId);

        VisTable texturePrintTable = new VisTable();
        VisLabel textures = new VisLabel("Texture IDs:");
        VisTextButton textButton = new VisTextButton("Print");
        texturePrintTable.add(textures);
        texturePrintTable.add(textButton).row();

        leftPane.add(texturePrintTable).row();

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(EntityCreator.class, "Hair: " + hairData.getData());
                println(EntityCreator.class, "Helm: " + helmData.getData());
                println(EntityCreator.class, "Chest: " + chestData.getData());
                println(EntityCreator.class, "Pants: " + pantsData.getData());
                println(EntityCreator.class, "Shoes: " + shoesData.getData());
            }
        });

        VisTable rightPane = new VisTable();

        setBodyPart(rightPane, "hair", 14, 16 * 3, 16 * 3, hairData, false);
        setBodyPart(rightPane, "helm", 40, 16 * 3, 16 * 3, helmData, true);
        setBodyPart(rightPane, "chest", 59, 16 * 3, 6 * 3, chestData, true);
        setBodyPart(rightPane, "pants", 59, 16 * 3, 3 * 3, pantsData, true);
        setBodyPart(rightPane, "shoes", 59, 16 * 3, 1 * 3, shoesData, true);

        hairColor = new VisSelectBox();
        hairColor.setItems(ColorList.values());
        hairColor.setSelectedIndex(0);
        listSelect(rightPane, "Hair: ", hairColor);

        eyeColor = new VisSelectBox();
        eyeColor.setItems(ColorList.values());
        eyeColor.setSelectedIndex(0);
        listSelect(rightPane, "Eyes: ", eyeColor);

        skinColor = new VisSelectBox();
        skinColor.setItems(ColorList.values());
        skinColor.setSelectedIndex(0);
        listSelect(rightPane, "Skin: ", skinColor);

        glovesColor = new VisSelectBox();
        glovesColor.setItems(ColorList.values());
        glovesColor.setSelectedIndex(0);
        listSelect(rightPane, "Gloves: ", glovesColor);

        add(leftPane).fill().pad(3);
        add(rightPane).fill().pad(3).row();

        characterPreview();
        add(previewTable).colspan(2).grow().row();

        setResizable(true);
        addCloseButton();
        centerWindow();
        setVisible(false);
        pack();
        return this;
    }

    private void setBodyPart(final VisTable visTable, final String texture, final int maxTextures, final int width, final int height, final ImageData imageData, boolean useCheckBox) {
        final int[] currentTexture = {0};

        final VisTable innerTable = new VisTable();
        final VisCheckBox visCheckBox = new VisCheckBox("Enable: ");
        final VisTextButton previous = new VisTextButton("<");
        final VisTable imageArea = new VisTable();
        imageArea.setWidth(width);
        imageArea.setHeight(height);
        imageArea.add(new ImageBuilder(GameAtlas.ENTITY_CHARACTER).setWidth(width).setHeight(height).setRegionName(texture + "_down_" + currentTexture[0]).buildVisImage());
        final VisTextButton next = new VisTextButton(">");
        final VisLabel textureId = new VisLabel("ID: " + currentTexture[0] + "/" + maxTextures);

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
                characterPreview();
            }
        });

        previous.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentTexture[0] == 0) {
                    currentTexture[0] = maxTextures;
                } else {
                    currentTexture[0] = currentTexture[0] - 1;
                }
                imageArea.clearChildren();
                imageArea.add(new ImageBuilder(GameAtlas.ENTITY_CHARACTER).setWidth(width).setHeight(height).setRegionName(texture + "_down_" + currentTexture[0]).buildVisImage());
                imageData.setData(currentTexture[0]);
                textureId.setText("ID: " + currentTexture[0] + "/" + maxTextures);
                pack();
                characterPreview();
            }
        });

        next.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentTexture[0] == maxTextures) {
                    currentTexture[0] = 0;
                } else {
                    currentTexture[0] = currentTexture[0] + 1;
                }
                imageArea.clearChildren();
                imageArea.add(new ImageBuilder(GameAtlas.ENTITY_CHARACTER).setWidth(width).setHeight(height).setRegionName(texture + "_down_" + currentTexture[0]).buildVisImage());
                imageData.setData(currentTexture[0]);
                textureId.setText("ID: " + currentTexture[0] + "/" + maxTextures);
                pack();
                characterPreview();
            }
        });

        if (useCheckBox) innerTable.add(visCheckBox);
        innerTable.add(previous);
        innerTable.add(imageArea);
        innerTable.add(next);
        innerTable.add(textureId);
        visTable.add(innerTable).row();
        pack();
    }

    private void listSelect(VisTable mainTable, String labelName, VisSelectBox visSelectBox) {
        VisTable selectTable = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        selectTable.add(visLabel).grow();
        selectTable.add(visSelectBox);
        mainTable.add(selectTable).expandX().fillX().row();

        visSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                characterPreview();
            }
        });
    }

    private void textField(VisTable mainTable, String labelName, VisTextField textField) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow();
        table.add(textField);
        mainTable.add(table).expandX().fillX().row();
    }

    private void characterPreview() {
        if (previewTable.hasChildren()) previewTable.clearChildren();
        final int scale = 10;
        final int width = 16 * scale;
        Color hair;
        Color eyes;
        Color skin;
        Color gloves;
        if (hairColor != null) {
            hair = ColorList.getType((byte) hairColor.getSelectedIndex()).getColor();
        } else {
            hair = Color.WHITE;
        }
        if (eyeColor != null) {
            eyes = ColorList.getType((byte) eyeColor.getSelectedIndex()).getColor();
        } else {
            eyes = Color.WHITE;
        }
        if (skinColor != null) {
            skin = ColorList.getType((byte) skinColor.getSelectedIndex()).getColor();
        } else {
            skin = Color.WHITE;
        }
        if (glovesColor != null) {
            gloves = ColorList.getType((byte) glovesColor.getSelectedIndex()).getColor();
        } else {
            gloves = Color.WHITE;
        }

        Stack imageStack = new Stack();
        imageStack.setWidth(16 * scale);
        imageStack.setHeight(16 * scale);

        imageStack.add(imageTable(scale, width, 16 * scale, 0, 0 * scale, "head_down_naked", skin));
        imageStack.add(imageTable(scale, width, 6 * scale, 0, 4 * scale, "chest_down_naked", skin));
        if (chestData.isUse())
            imageStack.add(imageTable(scale, width, 6 * scale, 0, 4 * scale, "chest_down_" + chestData.getData(), Color.WHITE));
        imageStack.add(imageTable(scale, width, 6 * scale, 0, 4 * scale, "gloves_down", gloves));
        imageStack.add(imageTable(scale, width, 3 * scale, 0, 1 * scale, "pants_down_naked", skin));
        if (pantsData.isUse())
            imageStack.add(imageTable(scale, width, 3 * scale, 0, 1 * scale, "pants_down_" + pantsData.getData(), Color.WHITE));
        imageStack.add(imageTable(scale, width, 1 * scale, 0, 0, "shoes_down_naked", skin));
        if (shoesData.isUse())
            imageStack.add(imageTable(scale, width, 1 * scale, 0, 0, "shoes_down_" + shoesData.getData(), Color.WHITE));
        imageStack.add(imageTable(scale, width, 16 * scale, 0, 1 * scale, "eyes_down", eyes));
        if (helmData.isUse()) {
            imageStack.add(imageTable(scale, width, 16 * scale, 0, 1 * scale, "helm_down_" + helmData.getData(), Color.WHITE));
        } else {
            imageStack.add(imageTable(scale, width, 16 * scale, 0, 1 * scale, "hair_down_" + hairData.getData(), hair));
        }

        previewTable.addActor(imageStack);
        previewTable.setWidth(16 * scale);
        previewTable.setHeight(16 * scale);
    }

    private VisTable imageTable(int scale, int width, int height, int x, int y, String region, Color color) {
        VisTable innerTable = new VisTable();
        innerTable.setWidth(16 * scale);
        innerTable.setHeight(16 * scale);
        innerTable.setFillParent(true);

        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ENTITY_CHARACTER);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureAtlas.findRegion(region));
        textureRegionDrawable.setMinWidth(width);
        textureRegionDrawable.setMinHeight(height);

        VisImage texture = new VisImage(textureRegionDrawable);
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setColor(color);
        texture.setX(x);
        texture.setY(y);

        innerTable.addActor(texture);
        return innerTable;
    }

    @Getter
    @Setter
    private class ImageData {
        int data = 0;
        boolean use;
    }
}
