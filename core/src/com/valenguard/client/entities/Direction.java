package com.valenguard.client.entities;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    STOP;

    public static Direction getDirection(byte directionByte) {
        for (Direction direction : Direction.values()) {
            if ((byte) direction.ordinal() == directionByte) {
                return direction;
            }
        }
        return null;
    }

    public byte getDirectionByte() {
        return (byte) this.ordinal();
    }
}
