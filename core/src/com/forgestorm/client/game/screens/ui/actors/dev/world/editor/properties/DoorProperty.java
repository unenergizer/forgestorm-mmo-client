package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class DoorProperty extends AbstractTileProperty {

    private Integer magicLockingLevel;

    public DoorProperty() {
        super(TilePropertyTypes.DOOR);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        VisLabel fieldLabel = new VisLabel("Magic Lock Level: ");
        final VisTextField magicLockLevelField = new VisTextField();
        if (magicLockingLevel != null) magicLockLevelField.setText(magicLockingLevel.toString());
        mainTable.add(fieldLabel).row();
        mainTable.add(magicLockLevelField);

        magicLockLevelField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (magicLockLevelField.isEmpty()) {
                    magicLockingLevel = 0;
                } else {
                    magicLockingLevel = Integer.valueOf(magicLockLevelField.getText());
                }
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        Integer magicLockingLevel = (Integer) tileProperties.get("magicLockingLevel");
        if (magicLockingLevel != null) setMagicLockingLevel(magicLockingLevel);

        println(getClass(), "magicLockingLevel: " + magicLockingLevel, false, printDebugMessages);
        return this;
    }
}
