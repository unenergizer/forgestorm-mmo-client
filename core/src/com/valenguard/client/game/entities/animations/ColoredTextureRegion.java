package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import lombok.Data;

@Data
public class ColoredTextureRegion {

    private Color regionColor = Color.WHITE;
    private TextureRegion textureRegion;
}
