package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Appearance {

    public static final int BODY = 0;
    public static final int HEAD = 1;
    public static final int ARMOR = 2;
    public static final int HELM = 3;

    @Getter
    @Setter
    private Color color;

    /**
     * IDs are arranged from head to toe or from top to bottom.
     */
    @Getter
    private short[] textureIds;

    public short getTextureId(int index) {
        return textureIds[index];
    }
}
