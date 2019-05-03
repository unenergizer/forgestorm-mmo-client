package com.valenguard.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import lombok.Data;

@Data
class ColoredTextureRegion {
    private Color regionColor = Color.WHITE;
    private TextureRegion textureRegion;
    private int yAxisOffset = 0;
}
