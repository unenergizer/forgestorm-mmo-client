package com.forgestorm.client.game.world.maps.tile.properties;

import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class LadderProperty extends AbstractTileProperty {

    public LadderProperty() {
        super(TilePropertyTypes.LADDER);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);
        mainTable.add(new VisLabel("[BROWN]Tile is a ladder."));
        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Tile is a ladder", false, printDebugMessages);
        return this;
    }
}
