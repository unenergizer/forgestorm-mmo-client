package com.valenguard.client.game.world.maps;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoveDirection {
    SOUTH("down"),
    EAST("right"),
    NORTH("up"),
    WEST("left"),
    NONE("down"); // Just defualt to down...

    private String directionName;

    public static MoveDirection getDirection(byte directionByte) {
        for (MoveDirection moveDirection : MoveDirection.values()) {
            if ((byte) moveDirection.ordinal() == directionByte) {
                return moveDirection;
            }
        }
        return null;
    }

    public byte getDirectionByte() {
        return (byte) this.ordinal();
    }
}
