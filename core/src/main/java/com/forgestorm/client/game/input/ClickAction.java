package com.forgestorm.client.game.input;

import com.forgestorm.client.game.world.entities.Entity;
import lombok.Getter;

@Getter
public class ClickAction {

    public static final byte LEFT = 0x01;
    public static final byte RIGHT = 0x02;

    private final byte clickAction;
    private final Entity clickedEntity;

    public ClickAction(byte clickAction, Entity entity) {
        this.clickAction = clickAction;
        this.clickedEntity = entity;
    }
}
