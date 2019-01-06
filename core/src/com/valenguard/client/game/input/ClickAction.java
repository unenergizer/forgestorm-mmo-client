package com.valenguard.client.game.input;

import lombok.Getter;

public class ClickAction {

    public static byte LEFT = 0x01;
    public static byte RIGHT = 0x02;

    @Getter
    private final byte clickAction;

    public ClickAction(byte clickAction) {
        this.clickAction = clickAction;
    }

}
