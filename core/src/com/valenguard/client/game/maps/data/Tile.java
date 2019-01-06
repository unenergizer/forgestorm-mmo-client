package com.valenguard.client.game.maps.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {

    public static final byte TRAVERSABLE = 0x01;
    public static final byte WARP = 0x02;

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
}
