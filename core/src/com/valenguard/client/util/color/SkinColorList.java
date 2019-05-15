package com.valenguard.client.util.color;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.util.RandomUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinColorList {

    SKIN_TONE_1(new Color(1, .913f, .77f, 1)),
    SKIN_TONE_2(new Color(.97f, .72f, .49f, 1)),
    SKIN_TONE_3(new Color(.85f, .43f, .32f, 1)),
    SKIN_TONE_4(new Color(.52f, .243f, .2f, 1)),
    SKIN_TONE_5(new Color(.24f, .12f, .12f, 1));

    private Color color;

    public static SkinColorList getType(byte typeByte) {
        for (SkinColorList libGDXColorList : SkinColorList.values()) {
            if ((byte) libGDXColorList.ordinal() == typeByte) {
                return libGDXColorList;
            }
        }
        return null;
    }

    public static Color randomColor() {
        return values()[RandomUtil.getNewRandom(0, values().length - 1)].getColor();
    }

    public byte getTypeByte() {
        return (byte) this.ordinal();
    }
}
