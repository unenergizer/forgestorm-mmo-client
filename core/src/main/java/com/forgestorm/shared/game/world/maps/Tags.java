package com.forgestorm.shared.game.world.maps;

import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.util.StringUtil;

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

    AN_UNUSED_TAG(new LayerDefinition[]{LayerDefinition.OVERHEAD, LayerDefinition.WORLD_OBJECT_DECORATION, LayerDefinition.WORLD_OBJECTS, LayerDefinition.GROUND_DECORATION, LayerDefinition.GROUND, LayerDefinition.BACKGROUND}),
    BUILDING_DECORATIONS(new LayerDefinition[]{LayerDefinition.WORLD_OBJECT_DECORATION}),
    CEMETERY(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    CONTAINER(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    DOORS(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    DOOR_LIGHTING(new LayerDefinition[]{LayerDefinition.GROUND_DECORATION}),
    DOOR_FRAMES(new LayerDefinition[]{LayerDefinition.OVERHEAD}),
    DUNGEON(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS, LayerDefinition.WORLD_OBJECT_DECORATION}),
    EGYPTIAN(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    FENCE(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    FLAG(new LayerDefinition[]{LayerDefinition.WORLD_OBJECT_DECORATION}),
    FURNITURE(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    KITCHEN(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS, LayerDefinition.WORLD_OBJECT_DECORATION}),
    OUTDOOR(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    PATH_WAY(new LayerDefinition[]{LayerDefinition.GROUND}),
    PLANTS(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    ROCKS(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    ROOF(new LayerDefinition[]{LayerDefinition.OVERHEAD}),
    SIGNS(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS, LayerDefinition.WORLD_OBJECT_DECORATION}),
    TABLE_TOP(new LayerDefinition[]{LayerDefinition.WORLD_OBJECT_DECORATION}),
    WALLS(new LayerDefinition[]{LayerDefinition.WORLD_OBJECTS}),
    WINDOW(new LayerDefinition[]{LayerDefinition.WORLD_OBJECT_DECORATION});

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
        return StringUtil.enumNameClean(name());
    }
}
