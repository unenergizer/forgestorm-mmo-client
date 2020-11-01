package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class WaterProperty extends AbstractTileProperty {

    @Getter
    @Setter
    private Boolean isWater;

    public WaterProperty() {
        super(TilePropertyTypes.WATER);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        final VisCheckBox isWaterCheckBox = new VisCheckBox("Water Flag", isWater != null && isWater);
        mainTable.add(isWaterCheckBox);

        isWaterCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isWater = isWaterCheckBox.isChecked();
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // Tile is water
        Boolean isWater = (Boolean) tileProperties.get("isWater");
        if (isWater != null) setIsWater(isWater);

        println(getClass(), "isWater: " + isWater, false, printDebugMessages);

        return this;
    }
}
