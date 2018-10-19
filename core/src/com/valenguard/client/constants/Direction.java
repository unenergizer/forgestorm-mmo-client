package com.valenguard.client.constants;

import lombok.Getter;

public enum Direction {
    UP((byte) 0x01),
    DOWN((byte) 0x02),
    LEFT((byte) 0x03),
    RIGHT((byte) 0x04),
    STOP((byte) 0x05);

    @Getter
    private byte direction;

    Direction(byte direction) {
        this.direction = direction;
    }
}
