package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.ProperName;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.util.color.LibGDXColorList;

import java.text.DecimalFormat;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

public class EntityCreator extends HideableVisWindow implements Buildable {

    private static final int PREVIEW_SCALE = 10;

    private final DecimalFormat decimalFormat = new DecimalFormat();

    private int moveDirection = 0;
    private VisSelectBox entityType;
    private VisTextField name = new VisValidatableTextField(new ProperName());
    private VisTextField health = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField damage = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField expDrop = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField dropTable = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisSlider walkSpeed = new VisSlider(.1f, 5, .1f, false);
    private VisSlider probStill = new VisSlider(0, 1, .1f, false);
    private VisSlider probWalk = new VisSlider(0, 1, .1f, false);
    private VisTextField shopId = new VisTextField();
    private ImageData hairData = new ImageData();
    private ImageData helmData = new ImageData();
    private ImageData chestData = new ImageData();
    private ImageData pantsData = new ImageData();
    private ImageData shoesData = new ImageData();
    private ColorPickerColorHandler hairColor;
    private ColorPickerColorHandler eyeColor;
    private ColorPickerColorHandler skinColor;
    private ColorPickerColorHandler glovesColor;

    private VisTable previewTable = new VisTable();

    public EntityCreator() {
        super("EntityCreator");
    }

    @Override
    public Actor build() {

        decimalFormat.setMaximumFractionDigits(2);

        VisTable leftPane = new VisTable();

        textField(leftPane, "Name:", name);
        entityType = new VisSelectBox();
        entityType.setItems(EntityType.values());
        listSelect(leftPane, "EntityType: ", entityType);
        textField(leftPane, "Health:", health);
        textField(leftPane, "Damage:", damage);
        textField(leftPane, "ExpDrop:", expDrop);
        textField(leftPane, "DropTable:", dropTable);
        valueSlider(leftPane, "Walk Speed:", walkSpeed);
        valueSlider(leftPane, "Probability Still:", probStill);
        valueSlider(leftPane, "Probability Walk:", probWalk);
        textField(leftPane, "Shop ID:", shopId);

        VisTable texturePrintTable = new VisTable();
        VisLabel textures = new VisLabel("Texture IDs:");
        VisTextButton textButton = new VisTextButton("Print Details");
        texturePrintTable.add(textures);
        texturePrintTable.add(textButton).row();
        leftPane.add(texturePrintTable).row();

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(EntityCreator.class, "--- Settings ---");
                println(EntityCreator.class, "Name: " + name.getText());
                println(EntityCreator.class, "Type: " + entityType.getSelected());
                println(EntityCreator.class, "Health: " + health.getText());
                println(EntityCreator.class, "Damage: " + damage.getText());
                println(EntityCreator.class, "ExpDrop: " + expDrop.getText());
                println(EntityCreator.class, "DropTable: " + dropTable.getText());
                println(EntityCreator.class, "WalkSpeed: " + walkSpeed.getValue());
                println(EntityCreator.class, "Probability Still: " + probStill.getValue());
                println(EntityCreator.class, "Probability Walk: " + probWalk.getValue());
                println(EntityCreator.class, "ShopID: " + shopId.getText());
                println(EntityCreator.class, "--- Appearance ---");
                println(EntityCreator.class, "Hair: " + hairData.getData());
                println(EntityCreator.class, "Helm: " + helmData.getData());
                println(EntityCreator.class, "Chest: " + chestData.getData());
                println(EntityCreator.class, "Pants: " + pantsData.getData());
                println(EntityCreator.class, "Shoes: " + shoesData.getData());
                println(EntityCreator.class, "HairColor: " + hairColor.getFinishedColor());
                println(EntityCreator.class, "EyesColor: " + eyeColor.getFinishedColor());
                println(EntityCreator.class, "SkinColor: " + skinColor.getFinishedColor());
                println(EntityCreator.class, "GlovesColor: " + glovesColor.getFinishedColor());
            }
        });

        VisTable submitTable = new VisTable();
        VisTextButton spawnButton = new VisTextButton("Spawn");
        VisTextButton saveButton = new VisTextButton("Save to Database");
        submitTable.add(spawnButton);
        submitTable.add(saveButton);
        leftPane.add(submitTable).row();

        spawnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Networking
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Networking
            }
        });

        VisTable rightPane = new VisTable();
        int textureSelectScale = 3;

        setBodyPart(rightPane, "hair", 14, 16 * textureSelectScale, 16 * textureSelectScale, hairData, false);
        setBodyPart(rightPane, "helm", 40, 16 * textureSelectScale, 16 * textureSelectScale, helmData, true);
        setBodyPart(rightPane, "chest", 59, 16 * textureSelectScale, 6 * textureSelectScale, chestData, true);
        setBodyPart(rightPane, "pants", 59, 16 * textureSelectScale, 3 * textureSelectScale, pantsData, true);
        setBodyPart(rightPane, "shoes", 59, 16 * textureSelectScale, 1 * textureSelectScale, shoesData, true);

        VisSelectBox hairSelectBox = new VisSelectBox();
        hairSelectBox.setItems(LibGDXColorList.values());
        hairColor = new ColorPickerColorHandler() {
            @Override
            public void finish(Color newColor) {
                characterPreview();
            }

            @Override
            public void change(Color newColor) {
                characterPreview();
            }
        };
        colorPicker(rightPane, "Hair: ", hairSelectBox, hairColor);

        VisSelectBox eyeSelectBox = new VisSelectBox();
        eyeSelectBox.setItems(LibGDXColorList.values());
        eyeColor = new ColorPickerColorHandler() {
            @Override
            public void finish(Color newColor) {
                characterPreview();
            }

            @Override
            public void change(Color newColor) {
                characterPreview();
            }
        };
        colorPicker(rightPane, "Eyes: ", eyeSelectBox, eyeColor);

        VisSelectBox skinSelectBox = new VisSelectBox();
        skinSelectBox.setItems(LibGDXColorList.values());
        skinColor = new ColorPickerColorHandler() {
            @Override
            public void finish(Color newColor) {
                characterPreview();
            }

            @Override
            public void change(Color newColor) {
                characterPreview();
            }
        };
        colorPicker(rightPane, "Skin: ", skinSelectBox, skinColor);

        VisSelectBox glovesSelectBox = new VisSelectBox();
        glovesSelectBox.setItems(LibGDXColorList.values());
        glovesColor = new ColorPickerColorHandler() {
            @Override
            public void finish(Color newColor) {
                characterPreview();
            }

            @Override
            public void change(Color newColor) {
                characterPreview();
            }
        };
        colorPicker(rightPane, "Gloves: ", glovesSelectBox, glovesColor);

        VisTable rotatePreviewTable = new VisTable();
        VisTextButton rotateLeft = new VisTextButton("< Rotate Left");
        VisTextButton rotateRight = new VisTextButton("Rotate Right >");
        rotatePreviewTable.add(rotateLeft).fillX();
        rotatePreviewTable.add(rotateRight).fillX();

        rotateLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveDirection = moveDirection - 1;

                if (moveDirection < 0) {
                    moveDirection = 3;
                }
                characterPreview();
            }
        });
        rotateRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveDirection = moveDirection + 1;

                if (moveDirection > 3) {
                    moveDirection = 0;
                }
                characterPreview();
            }
        });
        rightPane.add(rotatePreviewTable).row();

        characterPreview();
        previewTable.setWidth(16 * PREVIEW_SCALE);
        previewTable.setHeight(16 * PREVIEW_SCALE);
        rightPane.add(previewTable);

        add(leftPane).fill().pad(3).grow().left().top();
        add(rightPane).fill().pad(3).grow().left().top().row();

        setResizable(false);
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
        final VisLabel textureId = new VisLabel("");
        if (currentTexture[0] > 9) {
            textureId.setText("ID: " + currentTexture[0] + "/" + maxTextures);
        } else {
            textureId.setText("ID: 0" + currentTexture[0] + "/" + maxTextures);
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
                if (currentTexture[0] > 9) {
                    textureId.setText("ID: " + currentTexture[0] + "/" + maxTextures);
                } else {
                    textureId.setText("ID: 0" + currentTexture[0] + "/" + maxTextures);
                }
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
                if (currentTexture[0] > 9) {
                    textureId.setText("ID: " + currentTexture[0] + "/" + maxTextures);
                } else {
                    textureId.setText("ID: 0" + currentTexture[0] + "/" + maxTextures);
                }
                pack();
                characterPreview();
            }
        });

        if (useCheckBox) innerTable.add(visCheckBox);
        innerTable.add(previous).pad(1);
        innerTable.add(imageArea).pad(1);
        innerTable.add(next).pad(1);
        innerTable.add(textureId).pad(1);
        visTable.add(innerTable).row();
        pack();
    }

    private void colorPicker(VisTable mainTable, String labelName, final VisSelectBox visSelectBox, final ColorPickerColorHandler colorPickerColorHandler) {
        VisTable visTable = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        VisTextButton visTextButton = new VisTextButton("Pick Color");
        visTable.add(visLabel).grow().pad(1);
        visTable.add(visSelectBox).pad(1);
        visTable.add(visTextButton).pad(1);
        mainTable.add(visTable).expandX().fillX().pad(1).row();

        visSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Color color = LibGDXColorList.getType((byte) visSelectBox.getSelectedIndex()).getColor();
                colorPickerColorHandler.doColorChange(color);
                colorPickerColorHandler.setFinishedColor(color);
            }
        });

        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.getStageHandler().getColorPickerController().show(colorPickerColorHandler);
            }
        });
    }

    private void listSelect(VisTable mainTable, String labelName, VisSelectBox visSelectBox) {
        VisTable selectTable = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        selectTable.add(visLabel).grow().pad(1);
        selectTable.add(visSelectBox).pad(1);
        mainTable.add(selectTable).expandX().fillX().pad(1).row();

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
        table.add(visLabel).grow().pad(1);
        table.add(textField).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    private void valueSlider(VisTable mainTable, String labelName, final VisSlider slider) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        final VisLabel sliderValue = new VisLabel(decimalFormat.format(slider.getValue()));
        table.add(visLabel).grow().pad(1);
        table.add(slider).pad(1);
        table.add(sliderValue).pad(1);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sliderValue.setText(decimalFormat.format(slider.getValue()));
            }
        });

        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    private void characterPreview() {
        if (previewTable.hasChildren()) previewTable.clearChildren();
        final int width = 16 * PREVIEW_SCALE;
        Color hair = Color.WHITE;
        Color eyes = Color.WHITE;
        Color skin = Color.WHITE;
        Color gloves = Color.WHITE;
        if (hairColor != null)
            hair = hairColor.getColorChange();
        if (eyeColor != null)
            eyes = eyeColor.getColorChange();
        if (skinColor != null)
            skin = skinColor.getColorChange();
        if (glovesColor != null)
            gloves = glovesColor.getColorChange();

        Stack imageStack = new Stack();
        imageStack.setWidth(16 * PREVIEW_SCALE);
        imageStack.setHeight(16 * PREVIEW_SCALE);

        String direction = getDirection();

        imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "head_" + direction + "_naked", skin));
        imageStack.add(imageTable(width, 6 * PREVIEW_SCALE, 4 * PREVIEW_SCALE, "chest_" + direction + "_naked", skin));
        if (chestData.isUse())
            imageStack.add(imageTable(width, 6 * PREVIEW_SCALE, 4 * PREVIEW_SCALE, "chest_" + direction + "_" + chestData.getData(), Color.WHITE));
        imageStack.add(imageTable(width, 6 * PREVIEW_SCALE, 4 * PREVIEW_SCALE, "gloves_" + direction, gloves));
        imageStack.add(imageTable(width, 3 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "pants_" + direction + "_naked", skin));
        if (pantsData.isUse())
            imageStack.add(imageTable(width, 3 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "pants_" + direction + "_" + pantsData.getData(), Color.WHITE));
        imageStack.add(imageTable(width, 1 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "shoes_" + direction + "_naked", skin));
        if (shoesData.isUse())
            imageStack.add(imageTable(width, 1 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "shoes_" + direction + "_" + shoesData.getData(), Color.WHITE));
        if (moveDirection != 2)
            imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "eyes_" + direction, eyes));
        if (helmData.isUse()) {
            imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "helm_" + direction + "_" + helmData.getData(), Color.WHITE));
        } else {
            imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "hair_" + direction + "_" + hairData.getData(), hair));
        }

        previewTable.add(imageStack);
    }

    private String getDirection() {
        return MoveDirection.getDirection((byte) moveDirection).getDirectionName();
    }

    private VisTable imageTable(int width, int height, int padBottom, String region, Color color) {
        VisTable innerTable = new VisTable();

        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ENTITY_CHARACTER);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureAtlas.findRegion(region));
        textureRegionDrawable.setMinWidth(width);
        textureRegionDrawable.setMinHeight(height);

        VisImage texture = new VisImage(textureRegionDrawable);
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setColor(color);

        innerTable.add(texture).expand().fillX().bottom().left().padBottom(padBottom);
        return innerTable;
    }

    @Getter
    @Setter
    private class ImageData {
        int data = 0;
        boolean use;
    }
}
