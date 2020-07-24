package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;
import com.forgestorm.client.io.type.GameAtlas;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class WorldBuilder extends HideableVisWindow implements Buildable {

    private StageHandler stageHandler;

    @Setter
    @Getter
    private TextureRegion worldBuilderTile;

    @Getter
    private String activeDrawLayer = "background";

    public WorldBuilder() {
        super("World Builder");
    }

    // /teleport Intreis test_map 0 0

    @Override
    public Actor build(StageHandler stageHandler) {
        this.stageHandler = stageHandler;

        VisLabel tileType = new VisLabel("[GREEN]Select Tile TrackType");
        VisTextButton roofObject = new VisTextButton("Roof");
        VisTextButton wallObject = new VisTextButton("Wall");
        VisTextButton doorObject = new VisTextButton("Door");
        VisTextButton decorationObject = new VisTextButton("Decoration");

        VisLabel debugLabel = new VisLabel("[RED]Debug Map");
        VisTextButton printMap = new VisTextButton("Print Map");

        VisTable visTable = new VisTable();
        visTable.add(tileType).growX().row();
        visTable.add(roofObject).growX().row();
        visTable.add(wallObject).growX().row();
        visTable.add(doorObject).growX().row();
        visTable.add(decorationObject).growX().row();
        visTable.add(debugLabel).growX().row();
        visTable.add(printMap).growX().row();

        roofObject.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
                worldBuilderTile = textureAtlas.findRegion("roof");
                activeDrawLayer = "overhead";
            }
        });

        wallObject.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
                worldBuilderTile = textureAtlas.findRegion("wall");
                activeDrawLayer = "walls";
            }
        });

        doorObject.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
                worldBuilderTile = textureAtlas.findRegion("door");
                activeDrawLayer = "decoration";
            }
        });

        decorationObject.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
                worldBuilderTile = textureAtlas.findRegion("decoration");
                activeDrawLayer = "decoration";
            }
        });

        final Class clazz = this.getClass();

        printMap.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TiledMap tiledMap = ClientMain.getInstance().getGameScreen().getMapRenderer().getTiledMap();
                MapLayers layers = tiledMap.getLayers();

                println(clazz, "" + tiledMap.toString());
                for (MapLayer mapLayer : layers) {
                    println(clazz, "Layer: " + mapLayer.getName());

                    if (mapLayer instanceof TiledMapTileLayer) {
                        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) mapLayer;
                        int width = tiledMapTileLayer.getWidth();
                        int height = tiledMapTileLayer.getHeight();

                        println(clazz, "TileWidth: " + tiledMapTileLayer.getTileWidth() + ", TileHeight: " + tiledMapTileLayer.getTileHeight());
                        println(clazz, "Width: " + width + ", Height: " + height);

                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(i, j);

                                if (cell != null) {
                                    TiledMapTile tile = cell.getTile();
                                    String location = "Tile[" + i + "/" + j + "]";
                                    println(clazz, location + " ID: " + tile.getId());
                                    println(clazz, location + " TextureRegion: " + tile.getTextureRegion());
                                }
                            }
                        }

                        println(true);
                    }
                }
            }
        });

        add(visTable);

        addCloseButton();
        stopWindowClickThrough();

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        findPosition();
        pack();
        return this;
    }

    private void findPosition() {
        ChatWindow chatWindow = stageHandler.getChatWindow();
        float y = chatWindow.getY() + chatWindow.getHeight() + 15;
        setPosition(StageHandler.WINDOW_PAD_X, y);
    }
}
