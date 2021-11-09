package com.forgestorm.shared.game.world.maps.building;

import com.forgestorm.shared.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LayerDefinition {

    OVERHEAD("overhead", "Tiles placed on this layer will appear over the players head."), // NO COLLISION
    WORLD_OBJECT_DECORATION("world_object_decoration", "Select this layer if you want to place a tile on a wall or an impassable surface."), // NO COLLISION
    WORLD_OBJECTS("world_objects", "Tiles placed here are on the same layer as the player. Doors, beds, plants, and other objects go here."),
    GROUND_DECORATION("ground_decoration", "Tiles such as rugs and other things that go under the players feet go here."), // NO COLLISION
    GROUND("ground", "Put grass, dirt, sand, and path tiles here."), // NO COLLISION
    BACKGROUND("background", "Tiles here are below everything else."); // NO COLLISION

    private final String layerName;
    private final String description;

    public static LayerDefinition getLayerDefinition(byte entityTypeByte) {
        for (LayerDefinition entityType : LayerDefinition.values()) {
            if ((byte) entityType.ordinal() == entityTypeByte) {
                return entityType;
            }
        }
        return null;
    }

    public byte getLayerDefinitionByte() {
        return (byte) this.ordinal();
    }

    @Override
    public String toString() {
        return StringUtil.enumNameClean(name());
    }
}
