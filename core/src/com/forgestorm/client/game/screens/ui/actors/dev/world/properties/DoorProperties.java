package com.forgestorm.client.game.screens.ui.actors.dev.world.properties;

import com.forgestorm.client.game.screens.ui.actors.dev.world.DecorationType;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class DoorProperties extends DecorationProperties {

    private String magicLockingLevel;

    public DoorProperties(DecorationType decorationType) {
        super(decorationType);
    }

    @Override
    public CustomTileProperties load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Door Tile Properties:", false, printDebugMessages);
        String magicLockingLevel = (String) tileProperties.get("magicLockingLevel");
        setMagicLockingLevel(magicLockingLevel);
        println(getClass(), " - Magic Locking Level: " + magicLockingLevel, false, printDebugMessages);
        return this;
    }
}
