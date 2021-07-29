package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Tags {

//    ROOF
//    WALL_DECORATION
//    COLLIDABLES
//    GROUND_DECORATION
//    GROUND
//    BACKGROUND

    AN_UNUSED_TAG(new LayerDefinition[]{LayerDefinition.ROOF, LayerDefinition.WALL_DECORATION, LayerDefinition.COLLIDABLES, LayerDefinition.GROUND_DECORATION, LayerDefinition.GROUND, LayerDefinition.BACKGROUND}),
    BUILDING_DECORATIONS(new LayerDefinition[]{LayerDefinition.WALL_DECORATION}),
    CEMETERY(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    CONTAINER(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    DOORS(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    DOOR_LIGHTING(new LayerDefinition[]{LayerDefinition.GROUND_DECORATION}),
    DOOR_FRAMES(new LayerDefinition[]{LayerDefinition.ROOF}),
    DUNGEON(new LayerDefinition[]{LayerDefinition.COLLIDABLES, LayerDefinition.WALL_DECORATION}),
    EGYPTIAN(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    FENCE(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    FLAG(new LayerDefinition[]{LayerDefinition.WALL_DECORATION}),
    FURNITURE(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    KITCHEN(new LayerDefinition[]{LayerDefinition.COLLIDABLES, LayerDefinition.WALL_DECORATION}),
    OUTDOOR(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    PATH_WAY(new LayerDefinition[]{LayerDefinition.GROUND}),
    PLANTS(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    ROCKS(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    ROOF(new LayerDefinition[]{LayerDefinition.ROOF}),
    SIGNS(new LayerDefinition[]{LayerDefinition.COLLIDABLES, LayerDefinition.WALL_DECORATION}),
    TABLE_TOP(new LayerDefinition[]{LayerDefinition.WALL_DECORATION}),
    WALLS(new LayerDefinition[]{LayerDefinition.COLLIDABLES}),
    WINDOW(new LayerDefinition[]{LayerDefinition.WALL_DECORATION});

    @Getter
    public LayerDefinition[] tagLayerDefinitions;

    /**
     * Hacky way to do a ValueOf implementation. Tags here could
     * be added and removed at will. If they are removed, we can
     * safely ignore them now. The real ValueOf() method throws
     * and exception.
     *
     * @param value The value of the enum to get.
     * @return a Tag if it exists, otherwise return null.
     */
    public static Tags valueOfEnum(String value) {
        for (Tags type : Tags.class.getEnumConstants()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

    public static Tags[] getLayerSpecificTags(LayerDefinition layerDefinition, boolean ignoreUnsedTag) {
        List<Tags> allowedTags = new ArrayList<Tags>();
        for (Tags tag : Tags.values()) {
            for (LayerDefinition layerDefinitionFound : tag.getTagLayerDefinitions()) {
                if (layerDefinition == layerDefinitionFound) allowedTags.add(tag);
            }
        }

        // Remove this tag. Useful for filtering it out in the build menu.
        if (ignoreUnsedTag) allowedTags.remove(Tags.AN_UNUSED_TAG);

        // Convert to basic array...
        Tags[] tags = new Tags[allowedTags.size()];
        for (int i = 0; i < allowedTags.size(); i++) tags[i] = allowedTags.get(i);

        return tags;
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
