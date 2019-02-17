package com.valenguard.client.game.rpg;

import com.badlogic.gdx.graphics.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityAlignment {
    HOSTILE(Color.RED),
    NEUTRAL(Color.YELLOW),
    FRIENDLY(Color.LIME);

    private final Color color;

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

