package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.TilePropertiesLoader;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.io.type.GameTexture;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.Map;

public class TilePropertiesEditor extends HideableVisWindow implements Buildable {

    private final ImageBuilder imageBuilder = new ImageBuilder();
    private final VisTable imageAreaTable = new VisTable();
    private final VisTable imageOptionsTable = new VisTable();

    private Map<Integer, TileImage> tileImageMap;
    private Array<TextureAtlas.AtlasRegion> regions;
    private Texture grid;
    private int regionIndexToProcess = 0;

    public TilePropertiesEditor() {
        super("Tile Properties Editor");
    }

    @Override
    public Actor build(StageHandler stageHandler) {

        // Load TileProperties.yaml
        TilePropertiesLoader tilePropertiesLoader = new TilePropertiesLoader();
        tileImageMap = tilePropertiesLoader.loadTileProperties();

        // Load Tiles atlas
        FileManager fileManager = ClientMain.getInstance().getFileManager();
        fileManager.loadAtlas(GameAtlas.TILES);
        TextureAtlas textureAtlas = fileManager.getAtlas(GameAtlas.TILES);
        regions = textureAtlas.getRegions();

        fileManager.loadTexture(GameTexture.GRID);
        grid = fileManager.getTexture(GameTexture.GRID);

        // Setup Editor
        VisTextButton previousEntry = new VisTextButton("Previous Entry");
        VisTextButton nextEntry = new VisTextButton("Next Entry");

        previousEntry.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionIndexToProcess--;
                if (regionIndexToProcess < 0) regionIndexToProcess = regions.size - 1;
                processImage();
            }
        });
        nextEntry.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionIndexToProcess++;
                if (regionIndexToProcess >= regions.size) regionIndexToProcess = 0;
                processImage();
            }
        });

        // Do initial image processes to show something on the screen...
        processImage();

        // Build button area
        VisTable buttonTable = new VisTable();
        buttonTable.add(previousEntry).align(Alignment.LEFT.getAlignment());
        buttonTable.add(nextEntry).align(Alignment.RIGHT.getAlignment());

        add(buttonTable).row();
        add(imageAreaTable).row();

        addCloseButton();
        pack();
        centerWindow();
        return this;
    }

    private void processImage() {
        imageAreaTable.clear();

        // Now processes the unprocessed images...
        TextureAtlas.AtlasRegion region = regions.get(regionIndexToProcess);

        // Is this image in the map?
        boolean isProcessed = false;
        TileImage tileImageFound = null;
        for (TileImage tileImage : tileImageMap.values()) {
            if (tileImage.getFileName().equals(region.name)) {
                isProcessed = true;
                tileImageFound = tileImage;
                break;
            }
        }

        // Show Region details
        VisTable detailsTable = new VisTable();
        detailsTable.add(new VisLabel("[YELLOW]Name: [WHITE]" + region.name)).pad(2).align(Alignment.LEFT.getAlignment());
        detailsTable.add(new VisLabel("[YELLOW]Processed: [WHITE]" + isProcessed)).pad(2).align(Alignment.LEFT.getAlignment()).row();
        detailsTable.add(new VisLabel("[YELLOW]Width: [WHITE]" + region.getRegionWidth())).pad(2).align(Alignment.LEFT.getAlignment());
        detailsTable.add(new VisLabel("[YELLOW]Height: [WHITE]" + region.getRegionHeight())).pad(2).align(Alignment.LEFT.getAlignment()).row();
        if (isProcessed)
            detailsTable.add(new VisLabel("[YELLOW]ImageID: [WHITE]" + tileImageFound.getImageId())).colspan(2).pad(2).align(Alignment.LEFT.getAlignment()).row();

        imageAreaTable.add(detailsTable).grow().row();

        VisTable imageTable = new VisTable();
        VisImage image = imageBuilder
                .setGameAtlas(GameAtlas.TILES)
                .setRegionName(region.name)
                .setWidth(region.getRegionWidth())
                .setHeight(region.getRegionHeight())
                .buildVisImage();
        imageTable.add(image).grow().bottom().left();

        Stack imageStack = new Stack();
        imageStack.add(new VisImage(grid));
        imageStack.add(imageTable);

        imageAreaTable.add(imageStack).row();

        final VisSelectBox<TileType> tileTypeSelectBox = new VisSelectBox<TileType>();
        tileTypeSelectBox.setItems(TileType.values());
        tileTypeSelectBox.setSelected(TileType.STATIC_IMAGE);
        imageAreaTable.add(tileTypeSelectBox).row();
        imageAreaTable.add(imageOptionsTable);

        if (isProcessed) tileTypeSelectBox.setSelected(tileImageFound.getTileType());

        tileTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                imageOptionsTable.clear(); // Clear previous data
                switch (tileTypeSelectBox.getSelected()) {
                    case CLIFF:
                        showCliffOptions();
                        break;
                    case CONTAINER:
                        showContainerOptions();
                        break;
                    case DOOR:
                        showDoorOptions();
                        break;
                    case WALL:
                        showWallOptions();
                        break;
                    case STATIC_IMAGE:
                    default:
                        break;
                }
            }
        });

        pack();
    }

    private void showCliffOptions() {
        imageOptionsTable.add(new VisLabel("showCliffOptions"));
        pack();
    }

    private void showContainerOptions() {
        imageOptionsTable.add(new VisLabel("showContainerOptions"));
        pack();
    }

    private void showDoorOptions() {
        imageOptionsTable.add(new VisLabel("showDoorOptions"));
        pack();
    }

    private void showWallOptions() {
        imageOptionsTable.add(new VisLabel("showWallOptions"));
        pack();
    }
}
