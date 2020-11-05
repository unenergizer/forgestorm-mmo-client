package com.forgestorm.client.game.screens.ui.actors.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.util.color.EyeColorList;
import com.forgestorm.client.util.color.HairColorList;
import com.forgestorm.client.util.color.LibGDXColorList;
import com.forgestorm.client.util.color.SkinColorList;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class CharacterPreviewer {

    /**
     * The multiplied scale of the Character preview.
     */
    private final int previewScale;

    /**
     * The main table that is added to the scene.
     */
    private VisTable mainTable = new VisTable();

    /**
     * This table will contain the generated preview image. Add this table to your scene.
     */
    private VisTable previewTable = new VisTable();

    /**
     * The direction the character is facing.
     */
    private byte moveDirectionByte = 0;

    /**
     * Last known used appearance.
     */
    private Appearance lastUsedAppearance;

    /**
     * @param previewScale How large the preview should be.
     */
    public CharacterPreviewer(int previewScale) {
        this.previewScale = previewScale;
    }

    public VisTable generatePreviewTable() {
        generateCharacterPreview(lastUsedAppearance, MoveDirection.getDirection(moveDirectionByte));

        VisTextButton rotateLeft = new VisTextButton("<-");
        VisTextButton rotateRight = new VisTextButton("->");

        mainTable.add(previewTable).colspan(2).row();
        mainTable.add(rotateLeft).align(Alignment.LEFT.getAlignment());
        mainTable.add(rotateRight).align(Alignment.RIGHT.getAlignment());

        rotateLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveDirectionByte = (byte) (moveDirectionByte - 1);

                if (moveDirectionByte < 0) {
                    moveDirectionByte = 3;
                }
                generateCharacterPreview(lastUsedAppearance, MoveDirection.getDirection(moveDirectionByte));
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 17);
            }
        });
        rotateRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveDirectionByte = (byte) (moveDirectionByte + 1);

                if (moveDirectionByte > 3) {
                    moveDirectionByte = 0;
                }
                generateCharacterPreview(lastUsedAppearance, MoveDirection.getDirection(moveDirectionByte));
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 17);
            }
        });

        mainTable.pack();
        return mainTable;
    }

    /**
     * Generates a Scene2D preview of a player or npc character.
     *
     * @param appearance       Contains data on what the entity will look like.
     * @param previewDirection The facing direction of the entity.
     */
    public void generateCharacterPreview(Appearance appearance, MoveDirection previewDirection) {
        previewTable.clearChildren(); // Clear previous image

        if (appearance == null) {
            appearance = generateBasicAppearance();
        }

        lastUsedAppearance = appearance;

        if (previewDirection == null) {
            previewDirection = MoveDirection.getDirection(moveDirectionByte);
        } else {
            moveDirectionByte = previewDirection.getDirectionByte();
        }

        assert previewDirection != null;
        final String direction = previewDirection.getDirectionName();
        final int width = 16 * this.previewScale;

        Stack imageStack = new Stack();
        imageStack.setWidth(16 * this.previewScale);
        imageStack.setHeight(16 * this.previewScale);

        // Head
        imageStack.add(imageTable(width, 16 * this.previewScale, this.previewScale, "head_" + direction + "_naked", lastUsedAppearance.getSkinColor()));

        // Chest
        imageStack.add(imageTable(width, 6 * this.previewScale, 4 * this.previewScale, "chest_" + direction + "_naked", lastUsedAppearance.getSkinColor()));
        if (lastUsedAppearance.getChestTexture() != -1) {
            imageStack.add(imageTable(width, 6 * this.previewScale, 4 * this.previewScale, "chest_" + direction + "_" + lastUsedAppearance.getChestTexture(), Color.WHITE));
        }

        // Gloves
        imageStack.add(imageTable(width, 6 * this.previewScale, 4 * this.previewScale, "gloves_" + direction + "", lastUsedAppearance.getGlovesColor()));

        // Pants
        imageStack.add(imageTable(width, 3 * this.previewScale, this.previewScale, "pants_" + direction + "_naked", lastUsedAppearance.getSkinColor()));
        if (lastUsedAppearance.getPantsTexture() != -1) {
            imageStack.add(imageTable(width, 3 * this.previewScale, this.previewScale, "pants_" + direction + "_" + lastUsedAppearance.getPantsTexture(), Color.WHITE));
        }

        // Shoes
        imageStack.add(imageTable(width, this.previewScale, this.previewScale, "shoes_" + direction + "_naked", lastUsedAppearance.getSkinColor()));
        if (lastUsedAppearance.getShoesTexture() != -1) {
            imageStack.add(imageTable(width, this.previewScale, this.previewScale, "shoes_" + direction + "_" + lastUsedAppearance.getShoesTexture(), Color.WHITE));
        }

        // Eyes
        if (previewDirection != MoveDirection.NORTH) {
            // Note: no eye image exist for north facing eyes (eyes not visible, as back
            // of head is shown). This will prevent NullPointerException.
            imageStack.add(imageTable(width, 16 * this.previewScale, this.previewScale, "eyes_" + direction + "", lastUsedAppearance.getEyeColor()));
        }

        // Helmet & Hair
        if (lastUsedAppearance.getHelmTexture() != -1) {
            imageStack.add(imageTable(width, 16 * this.previewScale, this.previewScale, "helm_" + direction + "_" + lastUsedAppearance.getHelmTexture(), Color.WHITE));
            imageStack.add(imageTable(width, 16 * this.previewScale, this.previewScale, "helm_border_" + direction + "_" + lastUsedAppearance.getHelmTexture(), lastUsedAppearance.getBorderColor()));
        } else {
            imageStack.add(imageTable(width, 16 * this.previewScale, this.previewScale, "hair_" + direction + "_" + lastUsedAppearance.getHairTexture(), lastUsedAppearance.getHairColor()));
            imageStack.add(imageTable(width, 16 * this.previewScale, this.previewScale, "hair_border_" + direction + "_" + lastUsedAppearance.getHairTexture(), lastUsedAppearance.getBorderColor()));
        }

        // Body Border
        imageStack.add(imageTable(width, 16 * this.previewScale, this.previewScale, "body_" + direction + "_border", lastUsedAppearance.getBorderColor()));

        previewTable.add(imageStack);
        mainTable.pack();
    }

    /**
     * Generates a VisTable that will hold the drawable images.
     *
     * @param width     The width of the VisTable.
     * @param height    The height of the VisTable.
     * @param padBottom This padding describes how far to move the image up in pixels.
     * @param region    The region we will be drawling.
     * @param color     The color of the region we will be drawling.
     * @return A VisTable that contains an image and appropriate sizing and position data.
     */
    private VisTable imageTable(int width, int height, int padBottom, String region, Color color) {
        VisTable innerTable = new VisTable();

        TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.ENTITY_CHARACTER);
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

    /**
     * Resets the PreviewTable, clearing the image entirely.
     */
    public void reset() {
        previewTable.clearChildren();
    }

    Appearance generateBasicAppearance() {
        Appearance appearance = new Appearance();
        appearance.setHairTexture((byte) 0);
        appearance.setHelmTexture((byte) -1);
        appearance.setChestTexture((byte) 1);
        appearance.setPantsTexture((byte) 1);
        appearance.setShoesTexture((byte) 1);
        appearance.setHairColor(HairColorList.CORAL.getColor());
        appearance.setEyeColor(EyeColorList.ROYAL.getColor());
        appearance.setSkinColor(SkinColorList.SKIN_TONE_0.getColor());
        appearance.setGlovesColor(LibGDXColorList.CLEAR.getColor());
        appearance.setLeftHandTexture((byte) -1);
        appearance.setRightHandTexture((byte) -1);

        return appearance;
    }

    Appearance generateInvisibleAppearance() {
        Appearance appearance = new Appearance();
        appearance.setHairTexture((byte) 0);
        appearance.setHelmTexture((byte) -1);
        appearance.setChestTexture((byte) -1);
        appearance.setPantsTexture((byte) -1);
        appearance.setShoesTexture((byte) -1);
        appearance.setHairColor(LibGDXColorList.CLEAR.getColor());
        appearance.setEyeColor(LibGDXColorList.CLEAR.getColor());
        appearance.setSkinColor(LibGDXColorList.CLEAR.getColor());
        appearance.setGlovesColor(Color.CLEAR);
        appearance.setLeftHandTexture((byte) -1);
        appearance.setRightHandTexture((byte) -1);
        appearance.setBorderColor(LibGDXColorList.CLEAR.getColor());

        return appearance;
    }
}
