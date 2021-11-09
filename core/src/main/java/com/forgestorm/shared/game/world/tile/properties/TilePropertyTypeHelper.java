package com.forgestorm.shared.game.world.tile.properties;

import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.AbstractTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.BlockMoveDirectionProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.CollisionBlockProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.ContainerProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.CursorDrawOverTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.DoorProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.InteractDamageProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.InteriorStairsProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.JumpToDirectionProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.LadderProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TileWalkOverSoundProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.WangTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.WaterProperty;

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
