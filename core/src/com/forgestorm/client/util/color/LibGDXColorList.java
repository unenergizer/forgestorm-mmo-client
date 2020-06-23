package com.forgestorm.client.util.color;

import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.util.RandomUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LibGDXColorList {

    PLAYER_DEFAULT(new Color(1, .913f, .77f, 1)),

    WHITE(new Color(1, 1, 1, 1)),
    LIGHT_GRAY(new Color(0xbfbfbfff)),
    GRAY(new Color(0x7f7f7fff)),
    DARK_GRAY(new Color(0x3f3f3fff)),
    BLACK(new Color(0, 0, 0, 1)),

    CLEAR(new Color(0, 0, 0, 0)),

    BLUE(new Color(0, 0, 1, 1)),
    NAVY(new Color(0, 0, 0.5f, 1)),
    ROYAL(new Color(0x4169e1ff)),
    SLATE(new Color(0x708090ff)),
    SKY(new Color(0x87ceebff)),
    CYAN(new Color(0, 1, 1, 1)),
    TEAL(new Color(0, 0.5f, 0.5f, 1)),

    GREEN(new Color(0x00ff00ff)),
    CHARTREUSE(new Color(0x7fff00ff)),
    LIME(new Color(0x32cd32ff)),
    FOREST(new Color(0x228b22ff)),
    OLIVE(new Color(0x6b8e23ff)),

    YELLOW(new Color(0xffff00ff)),
    GOLD(new Color(0xffd700ff)),
    GOLDENROD(new Color(0xdaa520ff)),
    ORANGE(new Color(0xffa500ff)),

    BROWN(new Color(0x8b4513ff)),
    TAN(new Color(0xd2b48cff)),
    FIREBRICK(new Color(0xb22222ff)),

    RED(new Color(0xff0000ff)),
    SCARLET(new Color(0xff341cff)),
    CORAL(new Color(0xff7f50ff)),
    SALMON(new Color(0xfa8072ff)),
    PINK(new Color(0xff69b4ff)),
    MAGENTA(new Color(1, 0, 1, 1)),

    PURPLE(new Color(0xa020f0ff)),
    VIOLET(new Color(0xee82eeff)),
    MAROON(new Color(0xb03060ff));

    private Color color;

    public static LibGDXColorList getType(byte typeByte) {
        for (LibGDXColorList libGDXColorList : LibGDXColorList.values()) {
            if ((byte) libGDXColorList.ordinal() == typeByte) {
                return libGDXColorList;
            }
        }
        return null;
    }

    public byte getTypeByte() {
        return (byte) this.ordinal();
    }

    public static Color randomColor() {
        return values()[RandomUtil.getNewRandom(0, values().length - 1)].getColor();
    }

    public static Color getColorFromOrdinal(int ordinalValue) {
        return values()[ordinalValue].getColor();
    }
}
