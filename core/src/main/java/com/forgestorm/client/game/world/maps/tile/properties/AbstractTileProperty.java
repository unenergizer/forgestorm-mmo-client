package com.forgestorm.client.game.world.maps.tile.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractTileProperty {

    @Getter
    private transient final TilePropertyTypes tilePropertyType;

    @Getter
    @Setter
    private transient TileImage tileImage;
    private transient VisTable editorTableContents;

    protected AbstractTileProperty(TilePropertyTypes tilePropertyType) {
        this.tilePropertyType = tilePropertyType;
    }

    public VisTable buildEditorTable() {
        editorTableContents = new VisTable(true);

        VisTable innerTable = buildActors();
        editorTableContents.add(innerTable).growX();

        VisImageButton removePropertyButton = new VisImageButton("close-window");
        editorTableContents.add(removePropertyButton).growY().align(Alignment.TOP_RIGHT.getAlignment()).row();
        editorTableContents.addSeparator().colspan(2).growX();

        removePropertyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editorTableContents.clear();
                editorTableContents.remove();
                tileImage.getTileProperties().remove(tilePropertyType);
            }
        });

        // Debug statement if TileImage is null.
        if (tileImage == null)
            editorTableContents.add(new VisLabel("[RED]WARNING: TileImage variable is NULL!!")).colspan(2);
        return editorTableContents;
    }

    abstract VisTable buildActors();

    public abstract AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages);
}
