package com.forgestorm.client.util.color;

import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.util.RandomUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

@Getter
@AllArgsConstructor
public enum SkinColorList {

    SKIN_TONE_0(new Color(1.0f, .88f, .84f, 1)),
    SKIN_TONE_1(new Color(.99f, .72f, .60f, 1)),
    SKIN_TONE_2(new Color(.81f, .54f, .44f, 1)),
    SKIN_TONE_3(new Color(.69f, .32f, .19f, 1)),
    SKIN_TONE_4(new Color(.44f, .15f, .10f, 1)),
    SKIN_TONE_5(new Color(.29f, .11f, .07f, 1));

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

    public static void printAll() {
        for (SkinColorList list : SkinColorList.values()) {
            println(SkinColorList.class,  "" + Color.rgba8888(list.getColor()));
        }
    }

    public byte getTypeByte() {
        return (byte) this.ordinal();
    }

    public static Color getColorFromOrdinal(int ordinalValue) {
        return values()[ordinalValue].getColor();
    }
}
