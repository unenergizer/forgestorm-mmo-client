package com.forgestorm.client.game.world.maps.building;

import java.util.ArrayList;
import java.util.List;

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

    ROOF("overhead", "Tiles placed in this layer will appear over the players head."), // NO COLLISION
    WALL_DECORATION("wall_decoration", "Select this layer if you want to place a tile on a wall or an impassable surface."), // NO COLLISION
    COLLIDABLES("wall", "This is an impassable surface. Walls, counters, tables, etc."), // map.get(WALL_LAYER).get(X + y * WIDTH);
    GROUND_DECORATION("ground_decoration", "Tiles placed here are on the same layer as the player. Beds, plants, objects, etc, go here."), // NO COLLISION (carpet, tall grass)
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

        // Clean the name
        List<String> wordList = new ArrayList<String>(2);
        for (String word : name().split("_")) {
            String name = word.toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            wordList.add(name);
        }

        // Build the name String
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < wordList.size(); i++) {
            name.append(wordList.get(i));
            if (i != wordList.size() - 1) name.append(" ");
        }

        return name.toString();
    }
}
