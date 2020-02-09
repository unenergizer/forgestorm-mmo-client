package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.io.FileManager;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.util.color.EyeColorList;
import com.valenguard.client.util.color.HairColorList;
import com.valenguard.client.util.color.LibGDXColorList;
import com.valenguard.client.util.color.SkinColorList;

import lombok.Getter;

public class CharacterPreviewer {

    /**
     * This table will contain the generated preview image. Add this table to your scene.
     */
    @Getter
    private VisTable previewTable = new VisTable();

    /**
     * Generates a Scene2D preview of a player or npc character.
     *
     * @param appearance    Contains data on what the entity will look like.
     * @param moveDirection The facing direction of the entity.
     * @param previewScale  How large the preview should be.
     * @return A VisTable containing the generated preview image.
     */
    public VisTable fillPreviewTable(Appearance appearance, MoveDirection moveDirection, int previewScale) {
        previewTable.clearChildren(); // Clear previous image

        final String direction = moveDirection.getDirectionName();
        final int width = 16 * previewScale;

        Stack imageStack = new Stack();
        imageStack.setWidth(16 * previewScale);
        imageStack.setHeight(16 * previewScale);

        // Head
        imageStack.add(imageTable(width, 16 * previewScale, previewScale, "head_" + direction + "_naked", appearance.getSkinColor()));

        // Chest
        imageStack.add(imageTable(width, 6 * previewScale, 4 * previewScale, "chest_" + direction + "_naked", appearance.getSkinColor()));
        if (appearance.getChestTexture() != -1) {
            imageStack.add(imageTable(width, 6 * previewScale, 4 * previewScale, "chest_" + direction + "_" + appearance.getChestTexture(), Color.WHITE));
        }

        // Gloves
        imageStack.add(imageTable(width, 6 * previewScale, 4 * previewScale, "gloves_" + direction + "", appearance.getGlovesColor()));

        // Pants
        imageStack.add(imageTable(width, 3 * previewScale, previewScale, "pants_" + direction + "_naked", appearance.getSkinColor()));
        if (appearance.getPantsTexture() != -1) {
            imageStack.add(imageTable(width, 3 * previewScale, previewScale, "pants_" + direction + "_" + appearance.getPantsTexture(), Color.WHITE));
        }

        // Shoes
        imageStack.add(imageTable(width, previewScale, previewScale, "shoes_" + direction + "_naked", appearance.getSkinColor()));
        if (appearance.getShoesTexture() != -1) {
            imageStack.add(imageTable(width, previewScale, previewScale, "shoes_" + direction + "_" + appearance.getShoesTexture(), Color.WHITE));
        }

        // Eyes
        if (moveDirection != MoveDirection.NORTH) {
            // Note: no eye image exist for north facing eyes (eyes not visible, as back
            // of head is shown). This will prevent NullPointerException.
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "eyes_" + direction + "", appearance.getEyeColor()));
        }

        // Helmet & Hair
        if (appearance.getHelmTexture() != -1) {
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "helm_" + direction + "_" + appearance.getHelmTexture(), Color.WHITE));
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "helm_border_" + direction + "_" + appearance.getHelmTexture(), Color.BLACK));
        } else {
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "hair_" + direction + "_" + appearance.getHairTexture(), appearance.getHairColor()));
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "hair_border_" + direction + "_" + appearance.getHairTexture(), Color.BLACK));
        }

        // Body Border
        imageStack.add(imageTable(width, 16 * previewScale, previewScale, "body_" + direction + "_border", Color.BLACK));

        previewTable.add(imageStack);

        return previewTable;
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

        FileManager fileManager = Valenguard.getInstance().getFileManager();

        fileManager.loadAtlas(GameAtlas.ENTITY_CHARACTER);

        TextureAtlas textureAtlas = fileManager.getAtlas(GameAtlas.ENTITY_CHARACTER);
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

    public Appearance generateBasicAppearance() {
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
}
