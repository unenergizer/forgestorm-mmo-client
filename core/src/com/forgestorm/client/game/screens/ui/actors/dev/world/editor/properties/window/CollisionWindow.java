package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.window;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.TilePropertiesEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.CollisionBlockProperty;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.ArrayList;
import java.util.List;

import static com.forgestorm.client.util.Log.println;

public class CollisionWindow extends HideableVisWindow {

    private static final boolean PRINT_DEBUG = false;

    private final VisTextButton confirmButton = new VisTextButton("Confirm");
    private final VisTextButton cancelButton = new VisTextButton("Cancel");
    private final VisTable editorTable = new VisTable();

    private CollisionBlockProperty collisionBlockProperty;
    private List<Boolean> collisionList;

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
                setVisible(false);
                collisionBlockProperty.setCollisionList(collisionList);
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });

        centerWindow();
        setVisible(false);
        addCloseButton();
        stopWindowClickThrough();
        return this;
    }

    public void loadTileImage(CollisionBlockProperty collisionBlockProperty, List<Boolean> loadedList, TextureAtlas.AtlasRegion atlasRegion) {
        this.collisionBlockProperty = collisionBlockProperty;
        editorTable.clearChildren();
        int tilesWide = atlasRegion.getRegionWidth() / ClientConstants.TILE_SIZE;
        int tilesTall = atlasRegion.getRegionHeight() / ClientConstants.TILE_SIZE;

        // Init or load existing list
        if (loadedList == null || loadedList.isEmpty()) {
            this.collisionList = new ArrayList<Boolean>();
            for (int i = 0; i < tilesWide * tilesTall; i++) this.collisionList.add(i, false);
        } else {
            this.collisionList = loadedList;
        }

        TextureRegion[][] textureRegions = atlasRegion.split(ClientConstants.TILE_SIZE, ClientConstants.TILE_SIZE);

        for (int row = 0; row < tilesTall; row++) {
            for (int column = 0; column < tilesWide; column++) {

                final int finalRow = row, finalColumn = column;
                final int index = finalRow + finalColumn * tilesTall;

                final VisImageButton visImageButton = new VisImageButton(new ImageBuilder(GameAtlas.TILES).setSize(ClientConstants.TILE_SIZE * 2).setTextureRegions(textureRegions, row, column).buildTextureRegionDrawable());
                visImageButton.setSize(ClientConstants.TILE_SIZE, ClientConstants.TILE_SIZE);
                if (collisionList.get(index)) {
                    visImageButton.setColor(Color.YELLOW);
                    visImageButton.getImage().setColor(Color.RED);
                }
                editorTable.add(visImageButton);

                visImageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (!collisionList.get(index)) {
                            collisionList.set(index, true);
                            visImageButton.setColor(Color.YELLOW);
                            visImageButton.getImage().setColor(Color.RED);
                        } else {
                            collisionList.set(index, false);
                            visImageButton.setColor(Color.WHITE);
                            visImageButton.getImage().setColor(Color.WHITE);
                        }
                        println(CollisionWindow.class, "Row: " + finalRow + ", Col: " + finalColumn + ", Index: " + index + ", Collision: " + collisionList.get(index), false, PRINT_DEBUG);
                    }
                });
            }
            editorTable.row();
        }

        setVisible(true);
        pack();
        findPosition();
    }

    private void findPosition() {
        toFront();
        TilePropertiesEditor window = ActorUtil.getStageHandler().getTilePropertiesEditor();
        // Set it to the top right of the editor window
        setPosition(window.getX() + window.getWidth() + StageHandler.WINDOW_PAD_X,
                window.getY() + window.getHeight() - getHeight());
    }
}
