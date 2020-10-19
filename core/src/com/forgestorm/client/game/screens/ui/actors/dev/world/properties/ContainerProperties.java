package com.forgestorm.client.game.screens.ui.actors.dev.world.properties;

import com.forgestorm.client.game.screens.ui.actors.dev.world.DecorationType;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class ContainerProperties extends DecorationProperties {

    private boolean isLootable;

    public ContainerProperties(DecorationType decorationType) {
        super(decorationType);
    }

    @Override
    public CustomTileProperties load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Container Tile Properties:", false, printDebugMessages);
        boolean isLootable = (Boolean) tileProperties.get("isLootable");
        setLootable(isLootable);
        println(getClass(), " - Is Lootable: " + isLootable, false, printDebugMessages);
        return this;
    }
}
