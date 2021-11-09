package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class ContainerProperty extends AbstractTileProperty {

    private Boolean isBreakable;

    public ContainerProperty() {
        super(TilePropertyTypes.INTERACTIVE_CONTAINER);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);
        final VisCheckBox isBreakableCheckBox = new VisCheckBox("Breakable?", isBreakable != null && isBreakable);
        mainTable.add(isBreakableCheckBox);

        isBreakableCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isBreakable = isBreakableCheckBox.isChecked();
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        Boolean isBreakable = (Boolean) tileProperties.get("isBreakable");
        if (isBreakable != null) setIsBreakable(isBreakable);

        println(getClass(), "isBreakable: " + isBreakable, false, printDebugMessages);
        return this;
    }
}
