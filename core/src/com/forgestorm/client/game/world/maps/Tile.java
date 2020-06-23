package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.ClientMain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {

    private int tileText = -1;

    public static final byte TRAVERSABLE = 0x01;
    public static final byte WARP = 0x02;
    public static final byte BANK_ACCESS = 0x04;

    private byte flags = 0x00;

    private CursorDrawType cursorDrawType = CursorDrawType.NO_DRAWABLE;

    public void addFlag(byte flag) {
        flags |= flag;
    }

    public void removeFlag(byte flag) {
        flags &= ~flag;
    }

    public boolean isFlagSet(byte flag) {
        return (flags & flag) != 0;
    }

    public String getTileText() {
        if (tileText != -1) {
            ClientMain.getInstance().getLanguageManager().getString(tileText);
        }
        return null;
    }
}
