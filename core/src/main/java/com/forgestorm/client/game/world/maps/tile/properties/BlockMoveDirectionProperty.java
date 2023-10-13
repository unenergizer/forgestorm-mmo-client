package com.forgestorm.client.game.world.maps.tile.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

@Getter
public class BlockMoveDirectionProperty extends AbstractTileProperty {

    @Setter
    private Boolean blockWalkNorth, blockWalkSouth, blockWalkWest, blockWalkEast;

    public BlockMoveDirectionProperty() {
        super(TilePropertyTypes.BLOCK_MOVE_DIRECTION);
    }

    @Override
    VisTable buildActors() {

        VisTable mainTable = new VisTable(true);

        // Block direction and jump directions
        VisTable leftTable = new VisTable(true);
        VisTable rightTable = new VisTable(true);
        final VisCheckBox blockWalkNorthCheckBox = new VisCheckBox("Block Walk North", blockWalkNorth != null && blockWalkNorth);
        final VisCheckBox blockWalkSouthCheckBox = new VisCheckBox("Block Walk South", blockWalkSouth != null && blockWalkSouth);
        final VisCheckBox blockWalkWestCheckBox = new VisCheckBox("Block Walk West", blockWalkWest != null && blockWalkWest);
        final VisCheckBox blockWalkEastCheckBox = new VisCheckBox("Block Walk East", blockWalkEast != null && blockWalkEast);
        leftTable.add(blockWalkNorthCheckBox).row();
        leftTable.add(blockWalkSouthCheckBox).row();
        rightTable.add(blockWalkWestCheckBox).row();
        rightTable.add(blockWalkEastCheckBox).row();
        mainTable.add(leftTable);
        mainTable.add(rightTable);

        blockWalkNorthCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                blockWalkNorth = blockWalkNorthCheckBox.isChecked();
            }
        });
        blockWalkSouthCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                blockWalkSouth = blockWalkSouthCheckBox.isChecked();
            }
        });
        blockWalkWestCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                blockWalkWest = blockWalkWestCheckBox.isChecked();
            }
        });
        blockWalkEastCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                blockWalkEast = blockWalkEastCheckBox.isChecked();
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // 4-way direction blocker
        Boolean blockWalkNorth = (Boolean) tileProperties.get("blockWalkNorth");
        Boolean blockWalkSouth = (Boolean) tileProperties.get("blockWalkSouth");
        Boolean blockWalkWest = (Boolean) tileProperties.get("blockWalkWest");
        Boolean blockWalkEast = (Boolean) tileProperties.get("blockWalkEast");
        if (blockWalkNorth != null) setBlockWalkNorth(blockWalkNorth);
        if (blockWalkSouth != null) setBlockWalkSouth(blockWalkSouth);
        if (blockWalkWest != null) setBlockWalkWest(blockWalkWest);
        if (blockWalkEast != null) setBlockWalkEast(blockWalkEast);

        println(getClass(), "blockWalkNorth: " + blockWalkNorth, false, printDebugMessages);
        println(getClass(), "blockWalkSouth: " + blockWalkSouth, false, printDebugMessages);
        println(getClass(), "blockWalkWest: " + blockWalkWest, false, printDebugMessages);
        println(getClass(), "blockWalkEast: " + blockWalkEast, false, printDebugMessages);

        return this;
    }
}
