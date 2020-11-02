package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class CollisionBlockProperty extends AbstractTileProperty {

    public CollisionBlockProperty() {
        super(TilePropertyTypes.COLLISION_BLOCK);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        VisLabel visLabel1 = new VisLabel("[RED]Contains Collision:");
        VisLabel visLabel2 = new VisLabel("[RED]Movement to this tile will be blocked.");
        mainTable.add(visLabel1).row();
        mainTable.add(visLabel2);

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Tile not traversable.", false, printDebugMessages);
        return this;
    }
}
