package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.window;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.io.type.GameAtlas;
import com.kotcrab.vis.ui.widget.VisImageButton;
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

    public void loadTileImage(TextureAtlas.AtlasRegion atlasRegion) {
        editorTable.clearChildren();
        int tilesWide = atlasRegion.getRegionWidth() / ClientConstants.TILE_SIZE;
        int tilesTall = atlasRegion.getRegionHeight() / ClientConstants.TILE_SIZE;


        TextureRegion[][] textureRegions = atlasRegion.split(ClientConstants.TILE_SIZE, ClientConstants.TILE_SIZE);

        for (int row = 0; row < tilesTall; row++) {
            for (int column = 0; column < tilesWide; column++) {
                final VisImageButton visImageButton = new VisImageButton(new ImageBuilder(GameAtlas.TILES).setSize(ClientConstants.TILE_SIZE * 2).setTextureRegions(textureRegions, row, column).buildTextureRegionDrawable());
                visImageButton.setSize(ClientConstants.TILE_SIZE, ClientConstants.TILE_SIZE);
                editorTable.add(visImageButton);

                visImageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                         visImageButton.getImage().setColor(Color.RED);
                         visImageButton.setColor(Color.RED);
                    }
                });
            }
            editorTable.row();
        }

        setVisible(true);
        pack();
    }
}
