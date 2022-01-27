package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.world.WorldObject;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Entity extends WorldObject {

    private EntityType entityType;

    /**
     * A unique ID given to this entity by the server.
     */
    private short serverEntityID;

    /**
     * The display name of this entity.
     */
    private String entityName;

    /**
     * Spawn location of the entity
     */
    private Location currentMapLocation;

    /**
     * The world this entity was last seen on.
     */
    private String worldName;

    /**
     * The appearance of the entity.
     */
    private Appearance appearance;

    public void say(String text) {
        ActorUtil.getStageHandler().getChatDialogue().drawText(text);
    }

    public GameWorld getGameMap() {
        return ClientMain.getInstance().getWorldManager().getGameWorld(worldName);
    }

    public Tile getGroundTile() {
         return getGameMap().getTile(
                 LayerDefinition.GROUND,
                 currentMapLocation.getX(),
                 currentMapLocation.getY(),
                 currentMapLocation.getZ());
    }
}
