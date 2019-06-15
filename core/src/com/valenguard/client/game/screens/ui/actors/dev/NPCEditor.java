package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.ProperName;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.NPC;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.out.AdminEditorNPCPacketOut;
import com.valenguard.client.util.color.LibGDXColorList;

import java.text.DecimalFormat;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

@SuppressWarnings("PointlessArithmeticExpression")
public class NPCEditor extends HideableVisWindow implements Buildable {

    private static final int PREVIEW_SCALE = 10;

    private final DecimalFormat decimalFormat = new DecimalFormat();

    private short entityIDNum = -1;

    private int moveDirection = 0;
    private VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private VisTextField name = new VisValidatableTextField(new ProperName());
    private VisTextField faction = new VisValidatableTextField(new ProperName());
    private VisTextField health = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField damage = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField expDrop = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField dropTable = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisSlider walkSpeed = new VisSlider(.1f, 5, .1f, false);
    private VisSlider probStill = new VisSlider(0, 1, .1f, false);
    private VisSlider probWalk = new VisSlider(0, 1, .1f, false);
    private VisTextField shopId = new VisTextField("-1");
    @Getter
    private boolean selectSpawnActivated = false;
    private VisTextButton selectSpawn = new VisTextButton("Select Spawn Location");
    private VisTextField mapName = new VisTextField();
    private VisTextField mapX = new VisTextField();
    private VisTextField mapY = new VisTextField();
    private BodyPart hairBodyPart;
    private BodyPart helmBodyPart;
    private BodyPart chestBodyPart;
    private BodyPart pantsBodyPart;
    private BodyPart shoesBodyPart;
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

    public NPCEditor() {
        super("NPC Creator");
    }

    void resetValues() {
        entityIDNum = -1;
        entityID.setText(Short.toString(entityIDNum));
        name.setText("");
        faction.setText("THE EMPIRE");
        health.setText("");
        damage.setText("");
        expDrop.setText("");
        dropTable.setText("");
        walkSpeed.setValue(0);
        probStill.setValue(0);
        probWalk.setValue(0);
        shopId.setText("-1");
        selectSpawnActivated = false;
        mapName.setText("");
        mapX.setText("");
        mapY.setText("");

        // Appearance Data
        hairBodyPart.setData(0);
        helmBodyPart.setData(0, false);
        chestBodyPart.setData(0, false);
        pantsBodyPart.setData(0, false);
        shoesBodyPart.setData(0, false);
        hairData.reset();
        helmData.reset();
        chestData.reset();
        pantsData.reset();
        shoesData.reset();
        hairColor.setColor(LibGDXColorList.PLAYER_DEFAULT.getColor());
        eyeColor.setColor(LibGDXColorList.PLAYER_DEFAULT.getColor());
        skinColor.setColor(LibGDXColorList.PLAYER_DEFAULT.getColor());
        glovesColor.setColor(LibGDXColorList.PLAYER_DEFAULT.getColor());
    }

    public void loadNPC(NPC npc) {
        resetValues();
        entityIDNum = npc.getServerEntityID();
        entityID.setText(npc.getServerEntityID());

        name.setText(npc.getEntityName());
        // todo faction = faction.setText(npc.getFaction());
        health.setText(Integer.toString(npc.getMaxHealth()));
        damage.setText(Integer.toString(npc.getDamage()));
        expDrop.setText(Integer.toString(npc.getExpDrop()));
        dropTable.setText(Integer.toString(npc.getDropTable()));
        walkSpeed.setValue(npc.getMoveSpeed());
        probStill.setValue(npc.getProbWalkStill());
        probWalk.setValue(npc.getProbWalkStart());
        shopId.setText(Integer.toString(npc.getShopID()));
        mapName.setText(npc.getDefualtSpawnLocation().getMapName());
        mapX.setText(Short.toString(npc.getDefualtSpawnLocation().getX()));
        mapY.setText(Short.toString(npc.getDefualtSpawnLocation().getY()));

        Appearance appearance = npc.getAppearance();

        hairData.setData(appearance.getHairTexture());
        hairBodyPart.setData(appearance.getHairTexture());
        if (appearance.getHelmTexture() != -1) {
            helmData.setUse(true);
            helmData.setData(appearance.getHelmTexture());
            helmBodyPart.setData(appearance.getHelmTexture(), true);
        }
        if (appearance.getChestTexture() != -1) {
            chestData.setUse(true);
            chestData.setData(appearance.getChestTexture());
            chestBodyPart.setData(appearance.getChestTexture(), true);
        }
        if (appearance.getPantsTexture() != -1) {
            pantsData.setUse(true);
            pantsData.setData(appearance.getPantsTexture());
            pantsBodyPart.setData(appearance.getPantsTexture(), true);
        }
        if (appearance.getShoesTexture() != -1) {
            shoesData.setUse(true);
            shoesData.setData(appearance.getShoesTexture());
            shoesBodyPart.setData(appearance.getShoesTexture(), true);
        }

        hairColor.setColor(appearance.getHairColor());
        eyeColor.setColor(appearance.getEyeColor());
        skinColor.setColor(appearance.getSkinColor());
        glovesColor.setColor(appearance.getGlovesColor());

        characterPreview();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Actor build() {

        decimalFormat.setMaximumFractionDigits(2);

        /*
         * BEGIN LEFT PANE (DATA EDITOR) =================================================
         */
        VisTable leftPane = new VisTable();

        VisTable entityIdTable = new VisTable();
        VisLabel entityIDString = new VisLabel("EntityID: ");
        VisTextButton resetEntityID = new VisTextButton("Reset ID (create new entity)");
        entityIdTable.add(entityIDString).pad(3);
        entityIdTable.add(entityID).pad(3);
        entityIdTable.add(resetEntityID).pad(3);

        resetEntityID.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                entityIDNum = -1;
                entityID.setText(Short.toString(entityIDNum));
            }
        });

        leftPane.add(entityIdTable).row();

        textField(leftPane, "Name:", name);
        textField(leftPane, "Faction:", faction);
        textField(leftPane, "Health:", health);
        textField(leftPane, "Damage:", damage);
        textField(leftPane, "ExpDrop:", expDrop);
        textField(leftPane, "DropTable:", dropTable);
        valueSlider(leftPane, "Walk Speed:", walkSpeed);
        valueSlider(leftPane, "Probability Still:", probStill);
        valueSlider(leftPane, "Probability Walk:", probWalk);
        textField(leftPane, "Shop ID:", shopId);

        // Spawn location Selection
        mapName.setDisabled(true);
        selectSpawn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectSpawnActivated = true;
                mapName.setText(EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().getMapName());
                selectSpawn.setText("Left Click Map to Set Spawn");
                selectSpawn.setDisabled(true);
                Valenguard.getInstance().getMouseManager().setHighlightHoverTile(true);
//                ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getNPCEditor());
            }
        });

        ((GameScreen) Valenguard.getInstance().getScreen()).getMultiplexer().addProcessor(new InputProcessor() {

            private MouseManager mouseManager = Valenguard.getInstance().getMouseManager();

            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (!selectSpawnActivated) return false;
                if (button != Input.Buttons.LEFT) return false;
                selectSpawn.setText("Select Spawn Location");
                selectSpawn.setDisabled(false);
                mapX.setText(Short.toString(mouseManager.getLeftClickTileX()));
                mapY.setText(Short.toString(mouseManager.getLeftClickTileY()));
                selectSpawnActivated = false;
                Valenguard.getInstance().getMouseManager().setHighlightHoverTile(false);
//                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getNPCEditor());
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (!selectSpawnActivated) return false;
                mapX.setText(Short.toString(mouseManager.getMouseTileX()));
                mapY.setText(Short.toString(mouseManager.getMouseTileY()));
                return true;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                if (!selectSpawnActivated) return false;
                mapX.setText(Short.toString(mouseManager.getMouseTileX()));
                mapY.setText(Short.toString(mouseManager.getMouseTileY()));
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        });

        leftPane.add(selectSpawn).row();
        VisTable mapNameTable = new VisTable();
        mapNameTable.add(new VisLabel("Spawn Map:")).grow().pad(1);
        mapNameTable.add(mapName).pad(1);
        leftPane.add(mapNameTable).expandX().fillX().pad(1).row();

        VisTable mapXTable = new VisTable();
        mapXTable.add(new VisLabel("Spawn X:")).grow().pad(1);
        mapXTable.add(mapX).pad(1);
        leftPane.add(mapXTable).expandX().fillX().pad(1).row();

        VisTable mapYTable = new VisTable();
        mapYTable.add(new VisLabel("Spawn Y:")).grow().pad(1);
        mapYTable.add(mapY).pad(1);
        leftPane.add(mapYTable).expandX().fillX().pad(1).row();

        VisTable texturePrintTable = new VisTable();
        VisLabel textures = new VisLabel("DEBUG:");
        VisTextButton textButton = new VisTextButton("Print Details to Console");
        texturePrintTable.add(textures).pad(3);
        texturePrintTable.add(textButton).row();
        leftPane.add(texturePrintTable).row();

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(NPCEditor.class, "--- Settings ---");
                println(NPCEditor.class, "EntityID: " + entityID.getText());
                println(NPCEditor.class, "Name: " + name.getText());
                println(NPCEditor.class, "Faction: " + faction.getText());
                println(NPCEditor.class, "Health: " + health.getText());
                println(NPCEditor.class, "Damage: " + damage.getText());
                println(NPCEditor.class, "ExpDrop: " + expDrop.getText());
                println(NPCEditor.class, "DropTable: " + dropTable.getText());
                println(NPCEditor.class, "WalkSpeed: " + walkSpeed.getValue());
                println(NPCEditor.class, "Probability Still: " + probStill.getValue());
                println(NPCEditor.class, "Probability Walk: " + probWalk.getValue());
                println(NPCEditor.class, "ShopID: " + shopId.getText());
                println(NPCEditor.class, "SpawnLocation: " + mapName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText());
                println(NPCEditor.class, "--- Appearance ---");
                println(NPCEditor.class, "Hair: " + hairData.getData());
                println(NPCEditor.class, "Helm: " + helmData.getData());
                println(NPCEditor.class, "Chest: " + chestData.getData());
                println(NPCEditor.class, "Pants: " + pantsData.getData());
                println(NPCEditor.class, "Shoes: " + shoesData.getData());
                println(NPCEditor.class, "HairColor: " + hairColor.getFinishedColor());
                println(NPCEditor.class, "EyesColor: " + eyeColor.getFinishedColor());
                println(NPCEditor.class, "SkinColor: " + skinColor.getFinishedColor());
                println(NPCEditor.class, "GlovesColor: " + glovesColor.getFinishedColor());
            }
        });

        // Submit and finalize section
        VisTable submitTable = new VisTable();
        VisTextButton spawnButton = new VisTextButton("Spawn");
        VisTextButton saveButton = new VisTextButton("Save");
        VisTextButton resetButton = new VisTextButton("Reset");
        submitTable.add(spawnButton).pad(3);
        submitTable.add(saveButton).pad(3);
        submitTable.add(resetButton).pad(3);
        leftPane.add(submitTable).row();

        spawnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new AdminEditorNPCPacketOut(generateDataOut(false)).sendPacket();
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new AdminEditorNPCPacketOut(generateDataOut(true)).sendPacket();
                resetValues();
                ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getNPCEditor());
            }
        });

        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetValues();
            }
        });

        /*
         * BEGIN RIGHT PANE (VISUAL EDITOR) =================================================
         */
        VisTable rightPane = new VisTable();
        int textureSelectScale = 3;

        hairBodyPart = new BodyPart(rightPane, "hair", 14, 16 * textureSelectScale, 16 * textureSelectScale, hairData, false);
        helmBodyPart = new BodyPart(rightPane, "helm", 40, 16 * textureSelectScale, 16 * textureSelectScale, helmData, true);
        chestBodyPart = new BodyPart(rightPane, "chest", 59, 16 * textureSelectScale, 6 * textureSelectScale, chestData, true);
        pantsBodyPart = new BodyPart(rightPane, "pants", 59, 16 * textureSelectScale, 3 * textureSelectScale, pantsData, true);
        shoesBodyPart = new BodyPart(rightPane, "shoes", 59, 16 * textureSelectScale, 1 * textureSelectScale, shoesData, true);

        hairBodyPart.build();
        helmBodyPart.build();
        chestBodyPart.build();
        pantsBodyPart.build();
        shoesBodyPart.build();

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
                @SuppressWarnings("ConstantConditions") Color color = LibGDXColorList.getType((byte) visSelectBox.getSelectedIndex()).getColor();
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
            imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "helm_border_" + direction + "_" + helmData.getData(), Color.BLACK));
        } else {
            imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "hair_" + direction + "_" + hairData.getData(), hair));
            imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "hair_border_" + direction + "_" + hairData.getData(), Color.BLACK));
        }
        imageStack.add(imageTable(width, 16 * PREVIEW_SCALE, 1 * PREVIEW_SCALE, "body_" + direction + "_border", Color.BLACK));

        previewTable.add(imageStack);
    }

    private String getDirection() {
        //noinspection ConstantConditions
        return MoveDirection.getDirection((byte) moveDirection).getDirectionName();
    }

    @SuppressWarnings("SameParameterValue")
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

    private NPCEditorData generateDataOut(boolean save) {
        NPCEditorData npcEditorData = new NPCEditorData();

        npcEditorData.setSpawn(true);
        npcEditorData.setSave(save);

        // Basic data
        npcEditorData.setEntityID(entityIDNum);
        npcEditorData.setName(name.getText());
        npcEditorData.setFaction(faction.getText());
        npcEditorData.setHealth(Integer.valueOf(health.getText()));
        npcEditorData.setDamage(Integer.valueOf(damage.getText()));
        npcEditorData.setExpDrop(Integer.valueOf(expDrop.getText()));
        npcEditorData.setDropTable(Integer.valueOf(dropTable.getText()));
        npcEditorData.setWalkSpeed(walkSpeed.getValue());
        npcEditorData.setProbStop(probStill.getValue());
        npcEditorData.setProbWalk(probWalk.getValue());
        npcEditorData.setShopId(Short.valueOf(shopId.getText()));
        npcEditorData.setBankKeeper(false); // TODO

        // World data
        npcEditorData.setSpawnLocation(new Location(
                mapName.getText(),
                Short.valueOf(mapX.getText()),
                Short.valueOf(mapY.getText()))
        );

        // Appearance
        byte noEquip = -1;
        npcEditorData.setHairTexture((byte) hairData.getData());

        if (helmData.isUse()) {
            npcEditorData.setHelmTexture((byte) helmData.getData());
        } else {
            npcEditorData.setHelmTexture(noEquip);
        }

        if (chestData.isUse()) {
            npcEditorData.setChestTexture((byte) chestData.getData());
        } else {
            npcEditorData.setChestTexture(noEquip);
        }

        if (pantsData.isUse()) {
            npcEditorData.setPantsTexture((byte) pantsData.getData());
        } else {
            npcEditorData.setPantsTexture(noEquip);
        }

        if (shoesData.isUse()) {
            npcEditorData.setShoesTexture((byte) shoesData.getData());
        } else {
            npcEditorData.setShoesTexture(noEquip);
        }


        npcEditorData.setHairColor(hairColor.getFinishedColor());
        npcEditorData.setEyesColor(eyeColor.getFinishedColor());
        npcEditorData.setSkinColor(skinColor.getFinishedColor());
        npcEditorData.setGlovesColor(glovesColor.getFinishedColor());
        return npcEditorData;
    }

    private class BodyPart {

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

        private BodyPart(VisTable visTable, String texture, int maxTextures, int width, int height, ImageData imageData, boolean useCheckBox) {
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

        private void build() {
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
                    characterPreview();
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
                    pack();
                    characterPreview();
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
    }

    @Getter
    @Setter
    private class ImageData {
        int data = 0;
        boolean use;

        void reset() {
            data = 0;
            use = false;
        }
    }
}
