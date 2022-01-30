package com.forgestorm.client.game.world.maps.tile.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class TileWalkOverSoundProperty extends AbstractTileProperty {

    private TileWalkSound tileWalkSound;

    public TileWalkOverSoundProperty() {
        super(TilePropertyTypes.WALK_OVER_SOUND);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        final VisSelectBox<TileWalkSound> tileWalkSoundVisSelectBox = new VisSelectBox<>();
        tileWalkSoundVisSelectBox.setItems(TileWalkSound.values());
        tileWalkSoundVisSelectBox.setSelected(TileWalkSound.NONE);
        if (tileWalkSound != null) tileWalkSoundVisSelectBox.setSelected(tileWalkSound);

        mainTable.add(new VisLabel("Walk Over Sound:")).row();
        mainTable.add(tileWalkSoundVisSelectBox);

        tileWalkSoundVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tileWalkSound = tileWalkSoundVisSelectBox.getSelected();
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // Tile walk sound
        String tileWalkSound = (String) tileProperties.get("tileWalkSound");
        if (tileWalkSound != null) setTileWalkSound(TileWalkSound.valueOf(tileWalkSound));

        println(getClass(), "tileWalkSound: " + tileWalkSound, false, printDebugMessages);

        return this;
    }

    public enum TileWalkSound {
        NONE,
        BRICK,
        DIRT,
        GRASS,
        GRAVEL,
        SAND,
        STONE,
        WATER_SHALLOW,
        WATER_DEEP,
    }
}
