package com.forgestorm.client.game.world.maps.building;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LayerDefinition {

//    // public enum TileLayers.java
//    OVERHEAD,
//    DECORATION,
//    WALLS,
//    GROUND,
//    BACKGROUND

    ROOF("overhead"), // NO COLLISION
    WALL_DECORATION("wall_decoration"), // NO COLLISION
    COLLIDABLES("wall"), // map.get(WALL_LAYER).get(X + y * WIDTH);
    GROUND_DECORATION("ground_decoration"), // NO COLLISION (carpet, tall grass)
    GROUND("ground"), // NO COLLISION
    BACKGROUND("background"); // NO COLLISION

    private final String layerName;

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
}
