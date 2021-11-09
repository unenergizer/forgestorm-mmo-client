package com.forgestorm.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import lombok.Data;

@Data
public class ColoredTextureRegion {
    private Color regionColor = Color.WHITE;
    private TextureRegion textureRegion;
    private int xAxisOffset = 0;
    private int yAxisOffset = 0;
    private float width;
    private float height;
}
