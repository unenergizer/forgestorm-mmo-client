package com.forgestorm.client.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PixmapUtil {

    public static Pixmap createProceduralPixmap(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(color);
        pixmap.fill();

        return pixmap;
    }

    /**
     * Converts a texture region into a {@link Pixmap}. Mainly used for cursors.
     *
     * @param textureRegion The texture region to convert.
     * @return A {@link Pixmap} of the supplied texture region.
     */
    public static Pixmap extractPixmapFromTextureRegion(TextureRegion textureRegion, int sizeMultiplier) {
        TextureData textureData = textureRegion.getTexture().getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }
        Pixmap pixmap = new Pixmap(
                textureRegion.getRegionWidth() * sizeMultiplier,
                textureRegion.getRegionHeight() * sizeMultiplier,
                textureData.getFormat()
        );
        pixmap.drawPixmap(
                textureData.consumePixmap(), // The other Pixmap
                0, // The target x-coordinate (top left corner)
                0, // The target y-coordinate (top left corner)
                textureRegion.getRegionX(), // The source x-coordinate (top left corner)
                textureRegion.getRegionY(), // The source y-coordinate (top left corner)
                textureRegion.getRegionWidth() * sizeMultiplier, // The width of the area from the other Pixmap in pixels
                textureRegion.getRegionHeight() * sizeMultiplier // The height of the area from the other Pixmap in pixels
        );
        return pixmap;
    }

}
