package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.window;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import static com.forgestorm.client.util.Log.println;

public class CollisionWindow extends HideableVisWindow {

    private final VisTextButton confirmButton = new VisTextButton("Confirm");
    private final VisTextButton cancelButton = new VisTextButton("Cancel");
    private final VisTable editorTable = new VisTable();

    public CollisionWindow() {
        super("Collision Editor");
    }

    public Actor build() {
        VisTable buttonTable = new VisTable();
        buttonTable.add(confirmButton);
        buttonTable.add(cancelButton);

        add(editorTable).row();
        add(buttonTable);

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(getClass(), "Confirm clicked!");
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });

        setVisible(false);
        addCloseButton();
        stopWindowClickThrough();
        return this;
    }

    public void loadTileImage(TileImage tileImage, TextureAtlas.AtlasRegion atlasRegion) {
        editorTable.clearChildren();
        int tilesWide = atlasRegion.getRegionWidth() / 16;
        int tilesTall = atlasRegion.getRegionHeight() / 16;

        for (int i = 0; i < tilesWide; i++) {
            for (int j = 0; j < tilesTall; j++) {
                VisTextButton visTextButton = new VisTextButton(i + "," + j);
                visTextButton.setSize(16, 16);
                editorTable.add(visTextButton);
            }
        }

        setVisible(true);
    }
}
