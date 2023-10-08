package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.screens.ui.actors.dev.ColorPickerColorHandler;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.EntityEditorData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.NPCData;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.shared.game.world.maps.MoveDirection;
import com.forgestorm.shared.util.RandomNumberUtil;
import com.forgestorm.shared.util.color.LibGDXColorList;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("PointlessArithmeticExpression")
public class NPCAppearancePanel implements AppearancePanel {

    private static final int MAX_HAIR_TEXTURES = 14;
    private static final int MAX_HELM_TEXTURES = 40;
    private static final int MAX_CHEST_TEXTURES = 59;
    private static final int MAX_PANTS_TEXTURES = 59;
    private static final int MAX_SHOES_TEXTURES = 59;

    private final NpcTab npcTab;

    private int moveDirection = 0;
    private final NPCBodyPart hairBodyPart;
    private final NPCBodyPart helmBodyPart;
    private final NPCBodyPart chestBodyPart;
    private final NPCBodyPart pantsBodyPart;
    private final NPCBodyPart shoesBodyPart;
    private final ImageData hairData = new ImageData();
    private final ImageData helmData = new ImageData();
    private final ImageData chestData = new ImageData();
    private final ImageData pantsData = new ImageData();
    private final ImageData shoesData = new ImageData();
    private ColorPickerColorHandler hairColor;
    private ColorPickerColorHandler eyeColor;
    private ColorPickerColorHandler skinColor;
    private ColorPickerColorHandler glovesColor;

    NPCAppearancePanel(NpcTab npcTab) {
        this.npcTab = npcTab;
        int textureSelectScale = 3;

        hairBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "hair", MAX_HAIR_TEXTURES, 16 * textureSelectScale, 16 * textureSelectScale, hairData, false);
        helmBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "helm", MAX_HELM_TEXTURES, 16 * textureSelectScale, 16 * textureSelectScale, helmData, true);
        chestBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "chest", MAX_CHEST_TEXTURES, 16 * textureSelectScale, 6 * textureSelectScale, chestData, true);
        pantsBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "pants", MAX_PANTS_TEXTURES, 16 * textureSelectScale, 3 * textureSelectScale, pantsData, true);
        shoesBodyPart = new NPCBodyPart(npcTab, npcTab.getAppearanceTable(), "shoes", MAX_SHOES_TEXTURES, 16 * textureSelectScale, 1 * textureSelectScale, shoesData, true);

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

    @Override
    public void buildAppearancePanel() {
        final VisSelectBox<LibGDXColorList> hairSelectBox = new VisSelectBox<LibGDXColorList>();
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

        final VisSelectBox<LibGDXColorList> eyeSelectBox = new VisSelectBox<LibGDXColorList>();
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

        final VisSelectBox<LibGDXColorList> skinSelectBox = new VisSelectBox<LibGDXColorList>();
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

        final VisSelectBox<LibGDXColorList> glovesSelectBox = new VisSelectBox<LibGDXColorList>();
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

        // Randomize
        VisTextButton randomizeButton = new VisTextButton("Randomize");
        npcTab.getAppearanceTable().add(randomizeButton).row();

        randomizeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Clothes
                hairBodyPart.setData(RandomNumberUtil.getNewRandom(0, MAX_HAIR_TEXTURES));
                helmBodyPart.setData(RandomNumberUtil.getNewRandom(0, MAX_HELM_TEXTURES));
                chestBodyPart.setData(RandomNumberUtil.getNewRandom(0, MAX_CHEST_TEXTURES));
                pantsBodyPart.setData(RandomNumberUtil.getNewRandom(0, MAX_PANTS_TEXTURES));
                shoesBodyPart.setData(RandomNumberUtil.getNewRandom(0, MAX_SHOES_TEXTURES));

                // Skin and Hair
                hairSelectBox.setSelectedIndex(RandomNumberUtil.getNewRandom(0, LibGDXColorList.values().length - 1));
                eyeSelectBox.setSelectedIndex(RandomNumberUtil.getNewRandom(0, LibGDXColorList.values().length - 1));
                skinSelectBox.setSelectedIndex(RandomNumberUtil.getNewRandom(0, LibGDXColorList.values().length - 1));
                glovesSelectBox.setSelectedIndex(RandomNumberUtil.getNewRandom(0, LibGDXColorList.values().length - 1));

                // Rebuild the preview
                characterPreview();
            }
        });

        // Rotate
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
