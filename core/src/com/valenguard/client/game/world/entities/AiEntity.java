package com.valenguard.client.game.world.entities;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.rpg.EntityAlignment;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.util.GameTextUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiEntity extends MovingEntity {
    private short shopID;
    private EntityAlignment alignment;
    private boolean isBankKeeper;

    private Location defualtSpawnLocation;

    /**
     * Entity name drawing
     */
    @Override
    public void drawEntityName() {
        float x = getDrawX() + 8;
        float y = getDrawY() + 16 + ClientConstants.namePlateDistanceInPixels;

        GameTextUtil.drawMessage(getEntityName(), alignment.getColor(), .5f, x, y);
    }
}
