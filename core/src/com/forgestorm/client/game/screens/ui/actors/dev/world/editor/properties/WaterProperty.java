package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class WaterProperty extends AbstractTileProperty {

    public WaterProperty() {
        super(TilePropertyTypes.WATER);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);
        mainTable.add(new VisLabel("[BLUE]Tile is water."));
        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Tile is water.", false, printDebugMessages);
        return this;
    }
}
