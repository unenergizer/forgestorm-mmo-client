package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class WangTileProperty extends AbstractTileProperty {


    public WangTileProperty() {
        super(TilePropertyTypes.WANG_TILE);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);
        mainTable.add(new VisLabel("[BLUE]Tile is a wang tile."));
        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Tile is a wang tile.", false, printDebugMessages);
        return this;
    }

}
