package com.valenguard.client.game.world.maps;

public enum MoveDirection {
    NORTH,
    SOUTH,
    WEST,
    EAST,
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
