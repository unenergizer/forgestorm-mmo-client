package com.forgestorm.client.game.input;

import com.forgestorm.client.game.world.entities.Entity;

import lombok.Getter;

public class ClickAction {

    public static final byte LEFT = 0x01;
    public static final byte RIGHT = 0x02;

    @Getter
    private final byte clickAction;
    @Getter
    private final Entity clickedEntity;

    public ClickAction(byte clickAction, Entity entity) {
        this.clickAction = clickAction;
        this.clickedEntity = entity;
    }
}
