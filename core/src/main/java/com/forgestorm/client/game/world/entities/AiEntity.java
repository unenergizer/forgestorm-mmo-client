package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.EntityAlignment;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.util.GameTextUtil;
import com.forgestorm.shared.game.world.entities.FirstInteraction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiEntity extends MovingEntity {
    private final ClientMain clientMain;
    private short shopID;
    private EntityAlignment alignment;
    private boolean isBankKeeper;

    private Location defaultSpawnLocation;

    private FirstInteraction firstInteraction = FirstInteraction.ATTACK;

    public AiEntity(ClientMain clientMain) {
        super(clientMain);
        this.clientMain = clientMain;
    }

    /**
     * Entity name drawing
     */
    @Override
    public void drawEntityName() {
        float x = getDrawX() + 8;
        float y = getDrawY() + 16 + ClientConstants.namePlateDistanceInPixels;

        if (this.isPlayerClientTarget()) {
            GameTextUtil.drawMessage(clientMain, getEntityName(), alignment.getHighlightColor(), .5f, x, y);
        } else {
            GameTextUtil.drawMessage(clientMain, getEntityName(), alignment.getDefaultColor(), .5f, x, y);
        }
    }
}
