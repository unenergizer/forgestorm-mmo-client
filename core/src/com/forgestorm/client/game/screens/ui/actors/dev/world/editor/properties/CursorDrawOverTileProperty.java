package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.shared.game.world.maps.CursorDrawType;
import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypes;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class CursorDrawOverTileProperty extends AbstractTileProperty {

    private CursorDrawType cursorDrawType = CursorDrawType.NO_DRAWABLE;
    private VisTable imageTable;

    public CursorDrawOverTileProperty() {
        super(TilePropertyTypes.CURSOR_DRAW_OVER_TILE);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);
        imageTable = new VisTable();

        // Tile deals damage
        final VisSelectBox<CursorDrawType> cursorDrawTypeVisSelectBox = new VisSelectBox<CursorDrawType>();
        cursorDrawTypeVisSelectBox.setItems(CursorDrawType.values());
        cursorDrawTypeVisSelectBox.setSelected(CursorDrawType.NO_DRAWABLE);
        if (cursorDrawType != null) cursorDrawTypeVisSelectBox.setSelected(cursorDrawType);

        buildVisImage();

        mainTable.add(cursorDrawTypeVisSelectBox).colspan(2).row();
        mainTable.add(new VisLabel("[YELLOW]Preview:"));
        mainTable.add(imageTable);

        cursorDrawTypeVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cursorDrawType = cursorDrawTypeVisSelectBox.getSelected();
                buildVisImage();
            }
        });

        return mainTable;
    }

    public void buildVisImage() {
        imageTable.clear();
        if (cursorDrawType == null || cursorDrawType == CursorDrawType.NO_DRAWABLE) return;
        VisImage cursorPreview = new ImageBuilder(
                GameAtlas.CURSOR,
                cursorDrawType.getDrawableRegion(),
                cursorDrawType.getSize() * 2
        ).buildVisImage();
        imageTable.add(cursorPreview);
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // Take damage from walking over tile
        String cursorDrawType = (String) tileProperties.get("cursorDrawType");
        if (cursorDrawType != null) setCursorDrawType(CursorDrawType.valueOf(cursorDrawType));

        println(getClass(), "cursorDrawType: " + cursorDrawType, false, printDebugMessages);

        return this;
    }
}
