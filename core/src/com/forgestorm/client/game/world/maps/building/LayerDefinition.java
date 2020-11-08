package com.forgestorm.client.game.world.maps.building;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("unused")
@Getter
@AllArgsConstructor
public enum LayerDefinition {

//    // public enum TileLayers.java
//    OVERHEAD,
//    DECORATION,
//    WALLS,
//    GROUND,
//    BACKGROUND

    ROOF("overhead"),
    WALL_DECORATION("wall_decoration"),
    WALL("wall"),
    GROUND_DECORATION("ground_decoration"),
    GROUND("ground"),
    BACKGROUND("background");

    private String layerName;

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
