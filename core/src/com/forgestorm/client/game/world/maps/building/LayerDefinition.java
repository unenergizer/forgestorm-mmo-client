package com.forgestorm.client.game.world.maps.building;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("unused")
@Getter
@AllArgsConstructor
public enum LayerDefinition {

    ROOF("overhead"),
    WALL("walls"),
    WALL_DECORATION("walls"),
    GROUND("decoration"),
    GROUND_DECORATION("decoration");

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
