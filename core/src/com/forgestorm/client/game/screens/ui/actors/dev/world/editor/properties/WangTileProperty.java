package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class WangTileProperty extends AbstractTileProperty {

    @Getter
    @Setter
    private WangType wangType;

    public WangTileProperty() {
        super(TilePropertyTypes.WANG_TILE);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        // Wang tile setup
        VisTable wangTable = new VisTable(true);
        final VisSelectBox<WangType> wangTileTypeVisSelectBox = new VisSelectBox<WangType>();
        wangTileTypeVisSelectBox.setItems(WangType.values());
        wangTileTypeVisSelectBox.setSelected(WangType.WANG_16);
        if (wangType != null) wangTileTypeVisSelectBox.setSelected(wangType);

        VisTextButton setupWangTiles = new VisTextButton("Setup Wang Tiles");
        setupWangTiles.setDisabled(true);

        wangTable.add(wangTileTypeVisSelectBox);
        wangTable.add(setupWangTiles);
        mainTable.add(wangTable).row();

        wangTileTypeVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                wangType = wangTileTypeVisSelectBox.getSelected();
            }
        });
        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // Wang tiles
        String wangType = (String) tileProperties.get("wangType");
        if (wangType != null) setWangType(WangType.valueOf(wangType));

        println(getClass(), "wangType: " + wangType, false, printDebugMessages);

        return this;
    }

    private enum WangType {
        WANG_16,
        WANT_48
    }

}
