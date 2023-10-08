package com.forgestorm.client.game.world.maps.tile.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.forgestorm.shared.game.world.maps.tile.wang.BrushSize;
import com.forgestorm.shared.game.world.maps.tile.wang.WangType;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class WangTileProperty extends AbstractTileProperty {

    private transient int temporaryWangId;
    private transient String wangRegionNamePrefix;
    private WangType wangType;
    private BrushSize minimalBrushSize;

    public WangTileProperty() {
        super(TilePropertyTypes.WANG_TILE);
    }

    @SuppressWarnings("rawtypes")
    public void printDebug(Class clazz) {
        println(clazz, "temporaryWangId: " + temporaryWangId);
        println(clazz, "wangRegionNamePrefix: " + wangRegionNamePrefix);
        println(clazz, "wangType: " + wangType);
        println(clazz, "minimalBrushSize: " + minimalBrushSize);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);
        mainTable.add(new VisLabel("[BLUE]Tile is a wang tile.")).row();

        VisSelectBox<WangType> wangTypeVisSelectBox = new VisSelectBox<>();
        mainTable.add(wangTypeVisSelectBox).row();
        wangTypeVisSelectBox.setItems(WangType.values());
        if (wangType != null) {
            wangTypeVisSelectBox.setSelectedIndex(wangType.ordinal());
        } else {
            wangTypeVisSelectBox.setSelectedIndex(0);
        }

        wangTypeVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                wangType = wangTypeVisSelectBox.getSelected();
            }
        });

        VisSelectBox<BrushSize> brushSizeVisSelectBox = new VisSelectBox<>();
        mainTable.add(brushSizeVisSelectBox).row();
        brushSizeVisSelectBox.setItems(BrushSize.values());
        if (minimalBrushSize != null) {
            brushSizeVisSelectBox.setSelectedIndex(minimalBrushSize.ordinal());
        } else {
            brushSizeVisSelectBox.setSelectedIndex(0);
        }

        brushSizeVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                minimalBrushSize = brushSizeVisSelectBox.getSelected();
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Tile is a wang tile.", false, printDebugMessages);

        String wangType = (String) tileProperties.get("wangType");
        if (wangType != null) setWangType(WangType.valueOf(wangType));

        String minimalBrushSize = (String) tileProperties.get("minimalBrushSize");
        if (minimalBrushSize != null) setMinimalBrushSize(BrushSize.valueOf(minimalBrushSize));

        return this;
    }

}
