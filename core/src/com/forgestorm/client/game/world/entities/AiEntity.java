package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.rpg.EntityAlignment;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.util.GameTextUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiEntity extends MovingEntity {
    private short shopID;
    private EntityAlignment alignment;
    private boolean isBankKeeper;

    private Location defaultSpawnLocation;

    private FirstInteraction firstInteraction = FirstInteraction.ATTACK;

    /**
     * Entity name drawing
     */
    @Override
    public void drawEntityName() {
        float x = getDrawX() + 8;
        float y = getDrawY() + 16 + ClientConstants.namePlateDistanceInPixels;

        if (this.isPlayerClientTarget()) {
            GameTextUtil.drawMessage(getEntityName(), alignment.getHighlightColor(), .5f, x, y);
        } else {
            GameTextUtil.drawMessage(getEntityName(), alignment.getDefaultColor(), .5f, x, y);
        }
    }
}
