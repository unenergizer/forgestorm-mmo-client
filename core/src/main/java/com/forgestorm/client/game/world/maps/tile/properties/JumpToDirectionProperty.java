package com.forgestorm.client.game.world.maps.tile.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class JumpToDirectionProperty extends AbstractTileProperty {

    @Getter
    @Setter
    private Boolean canJumpNorth, canJumpSouth, canJumpWest, canJumpEast;

    public JumpToDirectionProperty() {
        super(TilePropertyTypes.JUMP_TO_DIRECTION);
    }

    @Override
    VisTable buildActors() {

        VisTable mainTable = new VisTable(true);

        VisTable leftTable = new VisTable(true);
        VisTable rightTable = new VisTable(true);
        final VisCheckBox canJumpNorthCheckBox = new VisCheckBox("Can Jump North", canJumpNorth != null && canJumpNorth);
        final VisCheckBox canJumpSouthCheckBox = new VisCheckBox("Can Jump South", canJumpSouth != null && canJumpSouth);
        final VisCheckBox canJumpWestCheckBox = new VisCheckBox("Can Jump West", canJumpWest != null && canJumpWest);
        final VisCheckBox canJumpEastCheckBox = new VisCheckBox("Can Jump East", canJumpEast != null && canJumpEast);
        leftTable.add(canJumpNorthCheckBox).row();
        leftTable.add(canJumpSouthCheckBox).row();
        rightTable.add(canJumpWestCheckBox).row();
        rightTable.add(canJumpEastCheckBox).row();
        mainTable.add(leftTable);
        mainTable.add(rightTable);

        canJumpNorthCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canJumpNorth = canJumpNorthCheckBox.isChecked();
            }
        });
        canJumpSouthCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canJumpSouth = canJumpSouthCheckBox.isChecked();
            }
        });
        canJumpWestCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canJumpWest = canJumpWestCheckBox.isChecked();
            }
        });
        canJumpEastCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canJumpEast = canJumpEastCheckBox.isChecked();
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // Tile side can be jumped off
        Boolean canJumpNorth = (Boolean) tileProperties.get("canJumpNorth");
        Boolean canJumpSouth = (Boolean) tileProperties.get("canJumpSouth");
        Boolean canJumpWest = (Boolean) tileProperties.get("canJumpWest");
        Boolean canJumpEast = (Boolean) tileProperties.get("canJumpEast");
        if (canJumpNorth != null) setCanJumpNorth(canJumpNorth);
        if (canJumpSouth != null) setCanJumpSouth(canJumpSouth);
        if (canJumpWest != null) setCanJumpWest(canJumpWest);
        if (canJumpEast != null) setCanJumpEast(canJumpEast);

        println(getClass(), "canJumpNorth: " + canJumpNorth, false, printDebugMessages);
        println(getClass(), "canJumpSouth: " + canJumpSouth, false, printDebugMessages);
        println(getClass(), "canJumpWest: " + canJumpWest, false, printDebugMessages);
        println(getClass(), "canJumpEast: " + canJumpEast, false, printDebugMessages);

        return this;
    }
}
