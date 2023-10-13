package com.forgestorm.shared.game.world.maps.tile.properties;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.tile.properties.*;

public class TilePropertyTypeHelper {
    public static AbstractTileProperty getNewAbstractTileProperty(ClientMain clientMain, TilePropertyTypes tilePropertyTypes) {
        switch (tilePropertyTypes) {
            case BLOCK_MOVE_DIRECTION:
                return new BlockMoveDirectionProperty();
            case COLLISION_BLOCK:
                return new CollisionBlockProperty(clientMain);
            case CURSOR_DRAW_OVER_TILE:
                return new CursorDrawOverTileProperty(clientMain);
            case DOOR:
                return new DoorProperty();
            case INTERACTIVE_CONTAINER:
                return new ContainerProperty();
            case INTERACT_DAMAGE:
                return new InteractDamageProperty();
            case INTERIOR_STAIRS_PROPERTY:
                return new InteriorStairsProperty(clientMain);
            case JUMP_TO_DIRECTION:
                return new JumpToDirectionProperty();
            case LADDER:
                return new LadderProperty();
            case WALK_OVER_SOUND:
                return new TileWalkOverSoundProperty();
            case WATER:
                return new WaterProperty();
            case WANG_TILE:
                return new WangTileProperty();
            default:
                throw new RuntimeException("TRIED TO ADD A PROPERTY THAT IS NOT IN THE SWITCH STATEMENT! ADD THE PROPERTY TO THE SWITCH!");
        }
    }

    public static boolean isPropertyStatefulSpecific(TilePropertyTypes tilePropertyTypes) {
        switch (tilePropertyTypes) {
            case DOOR:
            case INTERACTIVE_CONTAINER:
                return true;
            case BLOCK_MOVE_DIRECTION:
            case COLLISION_BLOCK:
            case CURSOR_DRAW_OVER_TILE:
            case INTERACT_DAMAGE:
            case INTERIOR_STAIRS_PROPERTY:
            case JUMP_TO_DIRECTION:
            case LADDER:
            case WALK_OVER_SOUND:
            case WATER:
            case WANG_TILE:
            default:
                return false;
        }
    }
}
