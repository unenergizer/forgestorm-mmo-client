package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class LadderProperty extends AbstractTileProperty {

    @Getter
    @Setter
    private Boolean isLadder;

    public LadderProperty() {
        super(TilePropertyTypes.LADDER);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        final VisCheckBox isLadderCheckBox = new VisCheckBox("Ladder Flag", isLadder != null && isLadder);
        mainTable.add(isLadderCheckBox);

        isLadderCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isLadder = isLadderCheckBox.isChecked();
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // Object is Ladder
        Boolean isLadder = (Boolean) tileProperties.get("isLadder");
        if (isLadder != null) setIsLadder(isLadder);

        println(getClass(), "isLadder: " + isLadder, false, printDebugMessages);
        return this;
    }
}
