package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.game.screens.ui.actors.dev.ColorPickerColorHandler;
import com.valenguard.client.game.screens.ui.actors.dev.entity.data.EntityEditorData;
import com.valenguard.client.game.screens.ui.actors.dev.entity.data.NPCData;
import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.util.color.LibGDXColorList;

import static com.valenguard.client.util.Log.println;

@SuppressWarnings("PointlessArithmeticExpression")
public class NPCAppearancePanel implements AppearancePanel {

    private final NpcTab npcTab;

    private int moveDirection = 0;
    private NPCBodyPart hairBodyPart;
    private NPCBodyPart helmBodyPart;
    private NPCBodyPart chestBodyPart;
    private NPCBodyPart pantsBodyPart;
    private NPCBodyPart shoesBodyPart;
    private ImageData hairData = new ImageData();
    private ImageData helmData = new ImageData();
    private ImageData chestData = new ImageData();
    private ImageData pantsData = new ImageData();
    private ImageData shoesData = new ImageData();
    private ColorPickerColorHandler hairColor;
    private ColorPickerColorHandler eyeColor;
    private ColorPickerColorHandler skinColor;
    private ColorPickerColorHandler glovesColor;

    NPCAppearancePanel(NpcTab npcTab) {
        this.npcTab = npcTab;
        int textureSelectScale = 3;

        hairBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "hair", 14, 16 * textureSelectScale, 16 * textureSelectScale, hairData, false);
        helmBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "helm", 40, 16 * textureSelectScale, 16 * textureSelectScale, helmData, true);
        chestBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "chest", 59, 16 * textureSelectScale, 6 * textureSelectScale, chestData, true);
        pantsBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "pants", 59, 16 * textureSelectScale, 3 * textureSelectScale, pantsData, true);
        shoesBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "shoes", 59, 16 * textureSelectScale, 1 * textureSelectScale, shoesData, true);

        hairBodyPart.build();
        helmBodyPart.build();
        chestBodyPart.build();
        pantsBodyPart.build();
        shoesBodyPart.build();
    }

    @Override
    public void load(AiEntity aiEntity) {
        Appearance appearance = aiEntity.getAppearance();

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
    }

    @Override
    public void reset() {
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

    @SuppressWarnings("unchecked")
    @Override
    public void buildAppearancePanel() {
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
        npcTab.colorPicker(npcTab.getAppearanceTable(), "Hair: ", hairSelectBox, hairColor);

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
        npcTab.colorPicker(npcTab.getAppearanceTable(), "Eyes: ", eyeSelectBox, eyeColor);

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
        npcTab.colorPicker(npcTab.getAppearanceTable(), "Skin: ", skinSelectBox, skinColor);

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
        npcTab.colorPicker(npcTab.getAppearanceTable(), "Gloves: ", glovesSelectBox, glovesColor);

        VisTable rotatePreviewTable = new VisTable();
        VisTextButton rotateLeft = new VisTextButton("< Rotate Left");
        VisTextButton rotateRight = new VisTextButton("Rotate Right >");
        rotatePreviewTable.add(rotateLeft).pad(3).fillX();
        rotatePreviewTable.add(rotateRight).pad(3).fillX();

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
        npcTab.getAppearanceTable().add(rotatePreviewTable).row();

        characterPreview();
        npcTab.getPreviewTable().setWidth(16 * NpcTab.PREVIEW_SCALE);
        npcTab.getPreviewTable().setHeight(16 * NpcTab.PREVIEW_SCALE);
        npcTab.getAppearanceTable().add(npcTab.getPreviewTable());
    }

    @Override
    public EntityEditorData getDataOut(EntityEditorData entityEditorData) {
        NPCData npcData = (NPCData) entityEditorData;

        byte noEquip = -1;
        npcData.setHairTexture((byte) hairData.getData());

        if (helmData.isUse()) {
            npcData.setHelmTexture((byte) helmData.getData());
        } else {
            npcData.setHelmTexture(noEquip);
        }

        if (chestData.isUse()) {
            npcData.setChestTexture((byte) chestData.getData());
        } else {
            npcData.setChestTexture(noEquip);
        }

        if (pantsData.isUse()) {
            npcData.setPantsTexture((byte) pantsData.getData());
        } else {
            npcData.setPantsTexture(noEquip);
        }

        if (shoesData.isUse()) {
            npcData.setShoesTexture((byte) shoesData.getData());
        } else {
            npcData.setShoesTexture(noEquip);
        }


        npcData.setHairColor(hairColor.getFinishedColor());
        npcData.setEyesColor(eyeColor.getFinishedColor());
        npcData.setSkinColor(skinColor.getFinishedColor());
        npcData.setGlovesColor(glovesColor.getFinishedColor());

        return npcData;
    }

    @Override
    public void characterPreview() {
        if (npcTab.getPreviewTable().hasChildren()) npcTab.getPreviewTable().clearChildren();
        final int width = 16 * NpcTab.PREVIEW_SCALE;
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
        imageStack.setWidth(16 * NpcTab.PREVIEW_SCALE);
        imageStack.setHeight(16 * NpcTab.PREVIEW_SCALE);

        String direction = getDirection();

        imageStack.add(npcTab.imageTable(width, 16 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "head_" + direction + "_naked", skin));
        imageStack.add(npcTab.imageTable(width, 6 * NpcTab.PREVIEW_SCALE, 4 * NpcTab.PREVIEW_SCALE, "chest_" + direction + "_naked", skin));
        if (chestData.isUse())
            imageStack.add(npcTab.imageTable(width, 6 * NpcTab.PREVIEW_SCALE, 4 * NpcTab.PREVIEW_SCALE, "chest_" + direction + "_" + chestData.getData(), Color.WHITE));
        imageStack.add(npcTab.imageTable(width, 6 * NpcTab.PREVIEW_SCALE, 4 * NpcTab.PREVIEW_SCALE, "gloves_" + direction, gloves));
        imageStack.add(npcTab.imageTable(width, 3 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "pants_" + direction + "_naked", skin));
        if (pantsData.isUse())
            imageStack.add(npcTab.imageTable(width, 3 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "pants_" + direction + "_" + pantsData.getData(), Color.WHITE));
        imageStack.add(npcTab.imageTable(width, 1 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "shoes_" + direction + "_naked", skin));
        if (shoesData.isUse())
            imageStack.add(npcTab.imageTable(width, 1 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "shoes_" + direction + "_" + shoesData.getData(), Color.WHITE));
        if (moveDirection != 2)
            imageStack.add(npcTab.imageTable(width, 16 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "eyes_" + direction, eyes));
        if (helmData.isUse()) {
            imageStack.add(npcTab.imageTable(width, 16 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "helm_" + direction + "_" + helmData.getData(), Color.WHITE));
            imageStack.add(npcTab.imageTable(width, 16 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "helm_border_" + direction + "_" + helmData.getData(), Color.BLACK));
        } else {
            imageStack.add(npcTab.imageTable(width, 16 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "hair_" + direction + "_" + hairData.getData(), hair));
            imageStack.add(npcTab.imageTable(width, 16 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "hair_border_" + direction + "_" + hairData.getData(), Color.BLACK));
        }
        imageStack.add(npcTab.imageTable(width, 16 * NpcTab.PREVIEW_SCALE, 1 * NpcTab.PREVIEW_SCALE, "body_" + direction + "_border", Color.BLACK));

        npcTab.getPreviewTable().add(imageStack);
    }

    private String getDirection() {
        //noinspection ConstantConditions
        return MoveDirection.getDirection((byte) moveDirection).getDirectionName();
    }

    @Override
    public void printDebug() {
        println(NPCAppearancePanel.class, "Hair: " + hairData.getData());
        println(NPCAppearancePanel.class, "Helm: " + helmData.getData());
        println(NPCAppearancePanel.class, "Chest: " + chestData.getData());
        println(NPCAppearancePanel.class, "Pants: " + pantsData.getData());
        println(NPCAppearancePanel.class, "Shoes: " + shoesData.getData());
        println(NPCAppearancePanel.class, "HairColor: " + hairColor.getFinishedColor());
        println(NPCAppearancePanel.class, "EyesColor: " + eyeColor.getFinishedColor());
        println(NPCAppearancePanel.class, "SkinColor: " + skinColor.getFinishedColor());
        println(NPCAppearancePanel.class, "GlovesColor: " + glovesColor.getFinishedColor());
    }
}
