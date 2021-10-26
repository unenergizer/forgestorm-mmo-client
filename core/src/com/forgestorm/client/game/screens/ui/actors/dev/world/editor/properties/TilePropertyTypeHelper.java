package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

public class TilePropertyTypeHelper {
    public static AbstractTileProperty getNewAbstractTileProperty(TilePropertyTypes tilePropertyTypes) {
        switch (tilePropertyTypes) {
            case BLOCK_MOVE_DIRECTION:
                return new BlockMoveDirectionProperty();
            case COLLISION_BLOCK:
                return new CollisionBlockProperty();
            case CURSOR_DRAW_OVER_TILE:
                return new CursorDrawOverTileProperty();
            case DOOR:
                return new DoorProperty();
            case INTERACTIVE_CONTAINER:
                return new ContainerProperty();
            case INTERACT_DAMAGE:
                return new InteractDamageProperty();
            case INTERIOR_STAIRS_PROPERTY:
                return new InteriorStairsProperty();
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
}
