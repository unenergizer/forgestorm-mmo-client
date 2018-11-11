package com.valenguard.client.game.maps;

public enum MoveDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE;

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
