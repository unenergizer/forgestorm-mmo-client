package com.valenguard.client.game.rpg;

import com.badlogic.gdx.graphics.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityAlignment {
    HOSTILE(Color.RED, Color.FIREBRICK),
    NEUTRAL(Color.YELLOW, Color.GOLD),
    FRIENDLY(Color.LIME, Color.FOREST);

    private final Color highlightColor;
    private final Color defaultColor;

    public static EntityAlignment getEntityAlignment(byte entityTypeByte) {
        for (EntityAlignment entityAlignment : EntityAlignment.values()) {
            if ((byte) entityAlignment.ordinal() == entityTypeByte) {
                return entityAlignment;
            }
        }
        return null;
    }

    public byte getEntityAlignmentByte() {
        return (byte) this.ordinal();
    }
}

