package com.forgestorm.client.game.screens.ui.actors.dev.world.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.LeftAlignTextButton;
import com.forgestorm.client.game.screens.ui.actors.dev.world.BuildCategory;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.AbstractTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.ItemDropDownMenu;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.util.yaml.YamlUtil;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

@Getter
public class TilePropertiesEditor extends HideableVisWindow implements Buildable {

    private static final String FILE_PATH = "C:/JavaThings/tileImageMap.yaml";

    private final TilePropertyDropDownMenu tilePropertyDropDownMenu = new TilePropertyDropDownMenu();
    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
    private final ImageBuilder imageBuilder = new ImageBuilder();

    private TileImage tileImage;
    private VisTable rightTable;
    private VisTable propertiesTable;
    private Map<TilePropertyTypes, AbstractTileProperty> copiedTileProperties;

    public TilePropertiesEditor() {
        super("Tile Properties Editor");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        stageHandler.getStage().addActor(tilePropertyDropDownMenu);
        tilePropertyDropDownMenu.build(stageHandler);

        VisTable leftTable = new VisTable(true);
        buildTabbedPaneTable(leftTable, GameAtlas.TILES);

        rightTable = new VisTable(true);
        buildOptionsTable(rightTable, null);

        add(leftTable).grow();
        add(rightTable).align(Alignment.TOP.getAlignment());

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        pack();
        setSize(leftTable.getWidth() + rightTable.getWidth() + 20, 400);
        centerWindow();
        setVisible(true);
        addCloseButton(new CloseButtonCallBack() {
            @Override
            public void closeButtonClicked() {
                tilePropertyDropDownMenu.fadeOut();
            }
        });
        setResizable(true);
        return this;
    }

    @SuppressWarnings("SameParameterValue")
    private void buildTabbedPaneTable(VisTable leftTable, GameAtlas gameAtlas) {
        final VisTable tabbedPaneTable = new VisTable();
        tabbedPaneTable.pad(3);

        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                tabbedPaneTable.clearChildren();
                tabbedPaneTable.add(tab.getContentTable()).expand().fill();
            }
        });

        leftTable.add(tabbedPane.getTable()).expandX().expandY().fillX().expandY().row();
        leftTable.add(tabbedPaneTable).expandY().fillX();

        // Add Build Categories
        tabbedPane.add(new TileBuildTab(gameAtlas));
        tabbedPane.switchTab(0);
        pack();
    }

    private void buildOptionsTable(VisTable rightTable, TextureAtlas.AtlasRegion atlasRegion) {
        if (atlasRegion == null) {
            rightTable.add(new VisLabel("[YELLOW]Please select a region on the left")).row();
            rightTable.add(new VisLabel("[YELLOW]to get started.")).row();
            return;
        }

        // Clear any previous option data
        rightTable.clear();

        // Is this image in the map?
        boolean isProcessed = false;
        for (TileImage ti : worldBuilder.getTileImageMap().values()) {
            if (ti.getFileName().equals(atlasRegion.name)) {
                isProcessed = true;
                tileImage = ti;
                break;
            }
        }

        if (tileImage == null) {
            tileImage = new TileImage(
                    worldBuilder.getTileImageMap().size(),
                    atlasRegion.name,
                    BuildCategory.UNDEFINED
            );
        }

        // Show Region details
        VisTable detailsTable = new VisTable(true);
        detailsTable.add(new VisLabel("[YELLOW]Name: [WHITE]" + atlasRegion.name)).align(Alignment.LEFT.getAlignment()).colspan(2).row();
        detailsTable.add(new VisLabel("[YELLOW]Width: [WHITE]" + atlasRegion.getRegionWidth())).align(Alignment.LEFT.getAlignment());
        detailsTable.add(new VisLabel("[YELLOW]Height: [WHITE]" + atlasRegion.getRegionHeight())).align(Alignment.LEFT.getAlignment()).row();
        detailsTable.add(new VisLabel("[YELLOW]Processed: [WHITE]" + isProcessed)).align(Alignment.LEFT.getAlignment());
        if (isProcessed) {
            detailsTable.add(new VisLabel("[YELLOW]ImageID: [WHITE]" + tileImage.getImageId())).colspan(2).align(Alignment.LEFT.getAlignment()).row();
        } else {
            detailsTable.row();
        }

        rightTable.add(detailsTable).row();

        // Show a larger image of the selected texture
        VisImage image = imageBuilder
                .setGameAtlas(GameAtlas.TILES)
                .setRegionName(atlasRegion.name)
                .setWidth(64)
                .setHeight(64)
                .buildVisImage();
        rightTable.add(image).row();

        // Add All Tile Property options.
        BuildCategory buildCategory = tileImage.getBuildCategory();

        VisTable buildCategoryTable = new VisTable(true);
        VisLabel buildCategoryLabel = new VisLabel("Build Category:");
        final VisSelectBox<BuildCategory> buildCategoryVisSelectBox = new VisSelectBox<BuildCategory>();
        buildCategoryVisSelectBox.setItems(BuildCategory.values());
        buildCategoryVisSelectBox.setSelected(BuildCategory.UNDEFINED);
        if (buildCategory != null) buildCategoryVisSelectBox.setSelected(buildCategory);

        buildCategoryTable.add(buildCategoryLabel);
        buildCategoryTable.add(buildCategoryVisSelectBox);
        rightTable.add(buildCategoryTable).row();

        buildCategoryVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tileImage.setBuildCategory(buildCategoryVisSelectBox.getSelected());
            }
        });

        // Add all individual selected options to the following table
        rightTable.add(buildPropertiesTable()).row();

        // Add table to let dev add properties to this TileImage
        VisTable addOptionsTable = new VisTable();
        final VisSelectBox<TilePropertyTypes> tilePropertiesVisSelectBox = new VisSelectBox<TilePropertyTypes>();
        tilePropertiesVisSelectBox.setItems(TilePropertyTypes.values());

        VisTextButton addTileProperty = new VisTextButton("Add TileProperty");

        addTileProperty.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Take selection and rebuild the options table
                TilePropertyTypes selectedProperty = tilePropertiesVisSelectBox.getSelected();

                // Check to make sure the TileImage does not already contain this property.
                if (tileImage.getTileProperties().containsKey(selectedProperty)) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(TilePropertiesEditor.class, (short) 10);
                    return;
                }

                // Now add the property and rebuild the options table
                AbstractTileProperty abstractTileProperty = selectedProperty.getAbstractTileProperty();
                abstractTileProperty.setTileImage(tileImage);
                tileImage.getTileProperties().put(selectedProperty, abstractTileProperty);
                buildPropertiesTable();
            }
        });

        addOptionsTable.add(new VisLabel("[YELLOW]Property to add:")).colspan(2).row();
        addOptionsTable.add(tilePropertiesVisSelectBox);
        addOptionsTable.add(addTileProperty);
        rightTable.add(addOptionsTable).row();

        VisTextButton saveFile = new VisTextButton("Save to File");
        rightTable.add(saveFile).row();

        saveFile.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(getClass(), "HashMap: " + worldBuilder.getTileImageMap());
                YamlUtil.saveYamlToFile(worldBuilder.getTileImageMap(), FILE_PATH);
//                Json json = new Json();
//                System.out.println(json.toJson(worldBuilder.getTileImageMap()));
            }
        });
    }

    private VisTable buildPropertiesTable() {
        if (propertiesTable == null) {
            propertiesTable = new VisTable();
        } else {
            propertiesTable.clear();
        }

        VisTable buttonTable = new VisTable();
        VisScrollPane scrollPane = new VisScrollPane(buttonTable);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setScrollingDisabled(true, false);
        propertiesTable.add(scrollPane).growX().expandY().top();

        // Add image buttons that represent the item. Filter by category.
        for (AbstractTileProperty abstractTileProperty : tileImage.getTileProperties().values()) {
            buttonTable.add(abstractTileProperty.buildEditorTable()).row();
        }

        scrollPane.layout();

        return propertiesTable;
    }

    @Getter
    private class TileBuildTab extends Tab {

        private final GameAtlas gameAtlas;
        private final TextureAtlas textureAtlas;
        private final String title;
        private final Table contentTable;

        TileBuildTab(GameAtlas gameAtlas) {
            super(false, false);
            this.gameAtlas = gameAtlas;
            this.textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(gameAtlas);
            this.title = " " + textureAtlas.getTextures().toString().substring(15) + " ";
            contentTable = new VisTable(true);
            build();
        }

        public void build() {
            // Create a scroll pane.
            VisTable buttonTable = new VisTable();
            VisScrollPane scrollPane = new VisScrollPane(buttonTable);
            scrollPane.setOverscroll(false, false);
            scrollPane.setFlickScroll(false);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollbarsOnTop(true);
            scrollPane.setScrollingDisabled(true, false);
            contentTable.add(scrollPane).growX().expandY().top();

            // Add image buttons that represent the item. Filter by category.
            int tilesAdded = 0;
            VisTable moduloTable = null;
            //noinspection LibGDXUnsafeIterator
            for (final TextureAtlas.AtlasRegion atlasRegion : textureAtlas.getRegions()) {
                if (tilesAdded % 7 == 0) {
                    // Create a new table every X amount of images added for scrolling purposes
                    moduloTable = new VisTable(true);
                    buttonTable.add(moduloTable).row();
                }
                tilesAdded++;
                final VisImageButton visImageButton = new VisImageButton(new ImageBuilder(gameAtlas, atlasRegion.name).setSize(32).buildTextureRegionDrawable());
                moduloTable.add(visImageButton);

                visImageButton.addListener(new InputListener() {
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if (button != Input.Buttons.RIGHT) return false;

                        // Open drop down menu for copy / paste.
                        Vector2 stageLocation = ActorUtil.getStageLocation(visImageButton);

                        // Setting X location
                        if (stageLocation.x > Gdx.graphics.getWidth() / 2f) {
                            tilePropertyDropDownMenu.setX(stageLocation.x - tilePropertyDropDownMenu.getWidth());
                        } else {
                            tilePropertyDropDownMenu.setX(stageLocation.x + tilePropertyDropDownMenu.getWidth());
                        }

                        // Setting Y location
                        if (stageLocation.y > Gdx.graphics.getHeight() / 2f) {
                            tilePropertyDropDownMenu.setY(stageLocation.y - tilePropertyDropDownMenu.getHeight());
                        } else {
                            tilePropertyDropDownMenu.setY(stageLocation.y + tilePropertyDropDownMenu.getHeight());
                        }

                        tilePropertyDropDownMenu.setVisible(true);
                        tilePropertyDropDownMenu.toFront();
                        return true;
                    }
                });

                visImageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        buildOptionsTable(rightTable, atlasRegion);
                    }
                });
            }

            scrollPane.layout();

            // TODO: Possibly add tool-tips to each one that describes it's properties?
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public Table getContentTable() {
            return contentTable;
        }
    }

    public class TilePropertyDropDownMenu extends HideableVisWindow implements Buildable {

        private HideableVisWindow hideableVisWindow;

        TilePropertyDropDownMenu() {
            super("Choose Option");
            this.hideableVisWindow = this;
        }

        @Override
        public Actor build(StageHandler stageHandler) {
            VisTable mainTable = new VisTable();
            LeftAlignTextButton copyButton = new LeftAlignTextButton("[YELLOW]Copy Tile Properties");
            LeftAlignTextButton pasteButton = new LeftAlignTextButton("[YELLOW]Paste Tile Properties");
            LeftAlignTextButton cancelButton = new LeftAlignTextButton("Cancel");

            mainTable.add(copyButton).expand().fill().row();
            mainTable.add(pasteButton).expand().fill().row();
            mainTable.add(cancelButton).expand().fill().row();
            hideableVisWindow.add(mainTable);

            copyButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    copiedTileProperties = new HashMap<TilePropertyTypes, AbstractTileProperty>(tileImage.getTileProperties());
                    hideableVisWindow.fadeOut();
                }
            });

            pasteButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tileImage.getTileProperties().putAll(copiedTileProperties);
                    buildPropertiesTable();
                    hideableVisWindow.fadeOut();
                }
            });

            cancelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                    hideableVisWindow.fadeOut();
                }
            });

            pack();
            setVisible(false);
            return this;
        }
    }
}
