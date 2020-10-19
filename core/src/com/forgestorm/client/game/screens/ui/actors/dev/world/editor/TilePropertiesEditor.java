package com.forgestorm.client.game.screens.ui.actors.dev.world.editor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.dev.world.BuildCategory;
import com.forgestorm.client.game.screens.ui.actors.dev.world.DecorationType;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.properties.ContainerProperties;
import com.forgestorm.client.game.screens.ui.actors.dev.world.properties.DecorationProperties;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.io.type.GameTexture;
import com.forgestorm.client.util.yaml.YamlUtil;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class TilePropertiesEditor extends HideableVisWindow implements Buildable {

    private static final String FILE_PATH = "C:/JavaThings/tileImageMap.yaml";

    private final WorldBuilder worldBuilder;

    private final ImageBuilder imageBuilder = new ImageBuilder();
    private final VisTable imageAreaTable = new VisTable(true);
    private final VisTable imageDetails = new VisTable(true);
    private final VisTable imageOptionsTable = new VisTable(true);

    private VisTextButton saveEntry = new VisTextButton("Save Entry");

    private Texture grid;
    private int regionIndexToProcess = 0;

    public TilePropertiesEditor() {
        super("Tile Properties Editor");
        worldBuilder = ClientMain.getInstance().getWorldBuilder();
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        FileManager fileManager = ClientMain.getInstance().getFileManager();
        fileManager.loadTexture(GameTexture.GRID);
        grid = fileManager.getTexture(GameTexture.GRID);


        // Build button area
        VisTable nextPreviousButtonTable = new VisTable(true);
        VisTextButton previousEntry = new VisTextButton("Previous Entry");
        VisTextButton nextEntry = new VisTextButton("Next Entry");

        nextPreviousButtonTable.add(previousEntry);
        nextPreviousButtonTable.add(nextEntry);

        previousEntry.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionIndexToProcess--;
                if (regionIndexToProcess < 0)
                    regionIndexToProcess = worldBuilder.getRegions().size - 1;
                processImage();
            }
        });
        nextEntry.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionIndexToProcess++;
                if (regionIndexToProcess >= worldBuilder.getRegions().size)
                    regionIndexToProcess = 0;
                processImage();
            }
        });


        // Save Buttons
        VisTable saveButtonTable = new VisTable(true);
        saveEntry.setDisabled(true);
        VisTextButton saveToFileButton = new VisTextButton("Save To File");

        saveButtonTable.add(saveEntry);
        saveButtonTable.add(saveToFileButton);

        saveToFileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Save all entries to file...
                YamlUtil.saveYamlToFile(worldBuilder.getTileImageMap(), FILE_PATH);
            }
        });

        // Do initial image processes to show something on the screen...
        processImage();

        // Add components to main actor
        add(nextPreviousButtonTable).row();
        add(imageAreaTable).row();
        add(saveButtonTable).row();

        stopWindowClickThrough();
        addCloseButton();
        pack();
        centerWindow();
        return this;
    }

    private void processImage() {
        imageAreaTable.clear();
        imageOptionsTable.clear();

        // Now processes the unprocessed images...
        final TextureAtlas.AtlasRegion region = worldBuilder.getRegions().get(regionIndexToProcess);

        // Is this image in the map?
        boolean isProcessed = false;
        TileImage tileImageFound = null;
        for (TileImage tileImage : worldBuilder.getTileImageMap().values()) {
            if (tileImage.getFileName().equals(region.name)) {
                isProcessed = true;
                tileImageFound = tileImage;
                break;
            }
        }

        // Show Region details
        VisTable detailsTable = new VisTable(true);
        detailsTable.add(new VisLabel("[YELLOW]Name: [WHITE]" + region.name)).align(Alignment.LEFT.getAlignment());
        detailsTable.add(new VisLabel("[YELLOW]Processed: [WHITE]" + isProcessed)).align(Alignment.LEFT.getAlignment()).row();
        detailsTable.add(new VisLabel("[YELLOW]Width: [WHITE]" + region.getRegionWidth())).align(Alignment.LEFT.getAlignment());
        detailsTable.add(new VisLabel("[YELLOW]Height: [WHITE]" + region.getRegionHeight())).align(Alignment.LEFT.getAlignment()).row();
        if (isProcessed) {
            detailsTable.add(new VisLabel("[YELLOW]ImageID: [WHITE]" + tileImageFound.getImageId())).colspan(2).align(Alignment.LEFT.getAlignment()).row();
        }

        imageAreaTable.add(detailsTable).grow().row();

        VisTable imageTable = new VisTable(true);
        VisImage image = imageBuilder
                .setGameAtlas(GameAtlas.TILES)
                .setRegionName(region.name)
                .setWidth(region.getRegionWidth())
                .setHeight(region.getRegionHeight())
                .buildVisImage();
        imageTable.add(image).grow().bottom().left();

        // Stack the image on top of the grid image
        Stack imageStack = new Stack();
        imageStack.add(new VisImage(grid));
        imageStack.add(imageTable);

        imageAreaTable.add(imageStack).row();

        // Build category
        VisTable buildCategoryTable = new VisTable(true);
        final VisSelectBox<BuildCategory> buildCategorySelectBox = new VisSelectBox<BuildCategory>();
        buildCategorySelectBox.setItems(BuildCategory.values());
        if (isProcessed) {
            buildCategorySelectBox.setSelected(tileImageFound.getBuildCategory());
        } else {
            buildCategorySelectBox.setSelected(BuildCategory.UNDEFINED);
        }
        buildCategoryTable.add(new VisLabel("Build Category: "));
        buildCategoryTable.add(buildCategorySelectBox);
        imageAreaTable.add(buildCategoryTable).row();

        buildCategorySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Build category selected... Lets redo some things
                processDetails(null, region.name, buildCategorySelectBox.getSelected());
            }
        });

        imageAreaTable.add(imageDetails);

        if (isProcessed) {
            processDetails(tileImageFound, region.name, tileImageFound.getBuildCategory());
        } else {
            processDetails(null, region.name, BuildCategory.UNDEFINED);
        }

        pack();
    }

    private void processDetails(TileImage tileImageFound, String regionName, BuildCategory buildCategory) {
        imageDetails.clear();
        saveEntry.setDisabled(true);
        switch (buildCategory) {
            case DECORATION:
                processDecorationCategory(tileImageFound, regionName);
                break;
            case TERRAIN:
                // TODO: Process terrain category
                break;
            case WALL:
                // TODO: Process wall category
                break;
            case ROOF:
                // TODO: Process roof category
                break;
            case UNDEFINED:
            default:
                imageDetails.add(new VisLabel("[RED] Please select a build category.")).row();
                break;
        }
    }

    private void processDecorationCategory(final TileImage tileImage, final String regionName) {
        VisTable decorationTypeTable = new VisTable(true);
        final VisSelectBox<DecorationType> decorationTypeSelectBox = new VisSelectBox<DecorationType>();
        decorationTypeSelectBox.setItems(DecorationType.values());
        decorationTypeSelectBox.setSelected(DecorationType.UNDEFINED);
        decorationTypeTable.add(new VisLabel("DecorationType: "));
        decorationTypeTable.add(decorationTypeSelectBox);

        imageDetails.add(decorationTypeTable).row();
        imageDetails.add(imageOptionsTable).row();

        if (tileImage != null) {
            decorationTypeSelectBox.setSelected(((DecorationProperties) tileImage.getCustomTileProperties()).getDecorationType());
        }

        decorationTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                doDecorationCat(tileImage, regionName, decorationTypeSelectBox.getSelected());
            }
        });

        doDecorationCat(tileImage, regionName, decorationTypeSelectBox.getSelected());
    }

    private void doDecorationCat(TileImage tileImageFound, String regionName, DecorationType decorationType) {
        imageOptionsTable.clear(); // Clear previous data
        switch (decorationType) {
            case BED:
                // TODO: Bed options
                break;
            case CHAIR:
                // TODO: Chair options
                break;
            case CONTAINER:
                showContainerOptions(tileImageFound, regionName);
                break;
            case TABLE:
                // TODO: Table options
                break;
            case UNDEFINED:
            default:
                imageOptionsTable.add(new VisLabel("[RED] Please select a decoration type."));
                break;
        }
    }


    private void showContainerOptions(TileImage tileImageFound, final String regionName) {
        imageOptionsTable.add(new VisLabel("Container Options:")).row();

        final VisCheckBox isLootable = new VisCheckBox("Lootable");
        if (tileImageFound != null) {
            if (tileImageFound.getCustomTileProperties() != null) {
                ContainerProperties customTileProperties = (ContainerProperties) tileImageFound.getCustomTileProperties();
                isLootable.setChecked(customTileProperties.isLootable());
            }
        } else {
            isLootable.setChecked(false);
        }
        imageOptionsTable.add(isLootable).row();

        saveEntry.setDisabled(false);
        saveEntry.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Get selected container options and save them to
                TileImage tileImage = new TileImage(
                        worldBuilder.getTileImageMap().size(),
                        regionName,
                        BuildCategory.DECORATION
                );
                ContainerProperties containerProperties = new ContainerProperties(DecorationType.CONTAINER);
                containerProperties.setLootable(isLootable.isChecked());
                tileImage.setCustomTileProperties(containerProperties);
                worldBuilder.addNewTile(tileImage);
            }
        });
    }
}
