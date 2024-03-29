package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.forgestorm.autotile.BrushType;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.LeftAlignTextButton;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.maps.RegionManager;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.game.world.maps.tile.properties.WangTileProperty;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.Tags;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.forgestorm.shared.game.world.maps.tile.wang.WangType;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

@Getter
public class TileBuildMenu extends HideableVisWindow implements Buildable {

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;
    private final WorldBuilder worldBuilder;
    private final RegionManager regionManager;
    private final Map<LayerDefinition, VisTextButton> layerButtonMap = new HashMap<>();
    private final List<VisImageButton> editorButtonList = new ArrayList<>();

    private VisImageButton drawlButton;
    private VisImageButton eraserButton;
    private VisImageButton wangBrushButton;
    private VisImageButton regionSelectButton;

    private TabbedPane tabbedPane;

    public TileBuildMenu(ClientMain clientMain) {
        super(clientMain, "World Build Menu");
        this.clientMain = clientMain;
        worldBuilder = clientMain.getWorldBuilder();
        regionManager = clientMain.getRegionManager();
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        TableUtils.setSpacingDefaults(this);
        addCloseButton(new CloseButtonCallBack() {
            @Override
            public void closeButtonClicked() {
                // Disable any world building tools when the window is closed
                activateOtherButton();
            }
        });
        setResizable(true);

        final ImageBuilder imageBuilder = new ImageBuilder(clientMain);
        final Drawable drawl = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_pencil").setSize(32).buildTextureRegionDrawable();
        final Drawable eraser = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_eraser").setSize(32).buildTextureRegionDrawable();
        final Drawable wangBrush = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_wang").setSize(32).buildTextureRegionDrawable();
        final Drawable runAllow = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_run_allow").setSize(32).buildTextureRegionDrawable();
        final Drawable runStop = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_run_stop").setSize(32).buildTextureRegionDrawable();
        final Drawable tileSelect = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_region_select").setSize(32).buildTextureRegionDrawable();

        final Drawable drawableActive = imageBuilder.setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_158").setSize(16).buildTextureRegionDrawable();
        final Drawable drawableInactive = imageBuilder.setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_159").setSize(16).buildTextureRegionDrawable();

        // BUILD TOOLS
        final VisTable toolsTable = new VisTable(true);
        drawlButton = new VisImageButton(drawl, "Drawl Tool");
        eraserButton = new VisImageButton(eraser, "Eraser Tool");
        wangBrushButton = new VisImageButton(wangBrush, "Wang Brush");
        final VisImageButton allowRunningButton = new VisImageButton(runAllow, "Allow/Prevent Click to Move");
        regionSelectButton = new VisImageButton(tileSelect, "Region Selection");

        drawlButton.setGenerateDisabledImage(true);
        eraserButton.setGenerateDisabledImage(true);
        wangBrushButton.setGenerateDisabledImage(true);
        allowRunningButton.setGenerateDisabledImage(true);
        regionSelectButton.setGenerateDisabledImage(true);

        editorButtonList.add(drawlButton);
        editorButtonList.add(eraserButton);
        editorButtonList.add(wangBrushButton);
        editorButtonList.add(allowRunningButton);
        editorButtonList.add(regionSelectButton);

        // Initialize build menu with drawl tool enabled first
        drawlButton.setDisabled(true);
        drawlButton.setColor(Color.RED);

        drawlButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                activateTool(Tools.DRAWL);
            }
        });

        eraserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                activateTool(Tools.ERASE);
            }
        });

        wangBrushButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                activateTool(Tools.WANG);
            }
        });

        allowRunningButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (worldBuilder.isAllowClickToMove()) {
                    allowRunningButton.getStyle().imageUp = runStop;
                    worldBuilder.setAllowClickToMove(false);
                } else {
                    allowRunningButton.getStyle().imageUp = runAllow;
                    worldBuilder.setAllowClickToMove(true);
                }
            }
        });

        regionSelectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                activateTool(Tools.REGION_SELECT);
            }
        });

        VisTable buttonTable = new VisTable();
        buttonTable.add(drawlButton);
        buttonTable.add(eraserButton);
        buttonTable.add(wangBrushButton);
        buttonTable.add(allowRunningButton);
        buttonTable.add(regionSelectButton);

        // AutoTile options
        VisCheckBox autoTileFixNeighborTiles = new VisCheckBox(": Fix neighbor auto-tiles");
        boolean defaultOption = true;
        autoTileFixNeighborTiles.setChecked(defaultOption);
        worldBuilder.getAutoTiler().setFixNeighborTiles(defaultOption);
        autoTileFixNeighborTiles.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                worldBuilder.getAutoTiler().setFixNeighborTiles(autoTileFixNeighborTiles.isChecked());
            }
        });

        VisTable brushTypeTable = new VisTable();
        VisSelectBox<BrushType> autoTileBrushType = new VisSelectBox<>();
        autoTileBrushType.setItems(BrushType.values());
        autoTileBrushType.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                worldBuilder.getAutoTiler().setBrushType(autoTileBrushType.getSelected());
            }
        });

        brushTypeTable.add(new VisLabel("Brush Type: "));
        brushTypeTable.add(autoTileBrushType);


        toolsTable.add(new VisLabel("[YELLOW]Tools:")).align(Alignment.LEFT.getAlignment()).row();
        toolsTable.add(buttonTable).align(Alignment.LEFT.getAlignment()).row();
        toolsTable.add(autoTileFixNeighborTiles).align(Alignment.LEFT.getAlignment()).row();
        toolsTable.add(brushTypeTable).align(Alignment.LEFT.getAlignment()).row();
        toolsTable.addSeparator();

        // LAYER SELECT
        final VisTable layerContainer = new VisTable();
        final VisTable layerSelectTable = new VisTable();
        layerContainer.add(new VisLabel("[YELLOW]Layer Select:")).align(Alignment.LEFT.getAlignment()).row();
        layerContainer.add(layerSelectTable);
        LayerDefinition[] values = LayerDefinition.values();
        for (int i = 0; i < values.length; i++) {
            final LayerDefinition layerDefinition = values[i];
            // Layer Visibility button
            final VisImageButton layerVisibilityButton = new VisImageButton(drawableActive, "Toggle Layer Visibility");
            layerSelectTable.add(layerVisibilityButton).padRight(3);
            layerVisibilityButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    boolean isVisible = worldBuilder.toggleLayerVisibility(layerDefinition);
                    if (isVisible) {
                        layerVisibilityButton.getStyle().imageUp = drawableActive;
                    } else {
                        layerVisibilityButton.getStyle().imageUp = drawableInactive;
                    }
                }
            });

            // Layer select button
            LeftAlignTextButton layerSelectButton = new LeftAlignTextButton(layerDefinition.toString());
            layerSelectTable.add(layerSelectButton).growX().padRight(3);
            layerSelectButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    worldBuilder.setCurrentLayer(layerDefinition);
                    resetButtons();
                    setSelectedLayerButton(layerDefinition);
                }
            });

            layerButtonMap.put(layerDefinition, layerSelectButton);

            // Layer Info button
            final Drawable layerInfo = imageBuilder.setGameAtlas(GameAtlas.ITEMS).setRegionName("quest_10" + i).setSize(16).buildTextureRegionDrawable();
            final VisImageButton layerInfoButton = new VisImageButton(layerInfo, "More info about this layer.");
            layerSelectTable.add(layerInfoButton).row();
            layerInfoButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Dialogs.showOKDialog(stageHandler.getStage(), "[YELLOW]Layer Information", "[GREEN]" + layerDefinition.getDescription());
                }
            });
        }

        // Now get the active layer, and disable that button (to indicate it's being used).
        // WorldBuilder class is setup first and the layer is decided then. Update the UI here.
        layerButtonMap.get(worldBuilder.getCurrentLayer()).setDisabled(true);

        // FLOOR SELECT
        final VisTable floorSelectTable = new VisTable();
        floorSelectTable.add(new VisLabel("[YELLOW]Floor Select:")).colspan(2).align(Alignment.LEFT.getAlignment()).row();

        final Map<Floors, LeftAlignTextButton> floorsVisTextButtonMap = new HashMap<>();

        for (final Floors floor : Floors.values()) {
            // Floor visibility

            final VisImageButton floorVisibilityButton = new VisImageButton(drawableActive, "Toggle Floor Visibility");
            floorVisibilityButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    boolean isVisible = worldBuilder.toggleFloorVisibility(floor);
                    if (isVisible) {
                        floorVisibilityButton.getStyle().imageUp = drawableActive;
                    } else {
                        floorVisibilityButton.getStyle().imageUp = drawableInactive;
                    }
                }
            });


            // Floor Select
            final LeftAlignTextButton visTextButton = new LeftAlignTextButton(floor.getName());
            visTextButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Reset floor button states
                    for (VisTextButton button : floorsVisTextButtonMap.values()) {
                        button.setDisabled(false);
                    }

                    worldBuilder.setCurrentWorkingFloor(floor);
                    visTextButton.setDisabled(true);
                }
            });

            floorSelectTable.add(floorVisibilityButton).padRight(3);
            floorSelectTable.add(visTextButton).growX().row();
            floorsVisTextButtonMap.put(floor, visTextButton);
        }

        // set default floor to disabled
        floorsVisTextButtonMap.get(worldBuilder.getCurrentWorkingFloor()).setDisabled(true);

        // LAYER AND FLOOR WRAPPER TABLE ADD....
        final VisTable layerFloorWrapperTable = new VisTable(true);
        layerFloorWrapperTable.add(layerContainer).align(Alignment.TOP.getAlignment());
        layerFloorWrapperTable.addSeparator(true).expandY();
        layerFloorWrapperTable.add(floorSelectTable).growX();

        // TABBED TILE SELECT TABLE
        final VisTable tabbedTableContainer = new VisTable();
        final VisTable tabbedTable = new VisTable();
        tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                tabbedTable.clearChildren();
                tabbedTable.add(tab.getContentTable()).expand().fill();
            }
        });

        tabbedTableContainer.add(tabbedPane.getTable()).expandX().fillX();
        tabbedTableContainer.row();
        tabbedTableContainer.add(tabbedTable).expand().fill();

        // Add Tabs (Layer Definitions)
        for (LayerDefinition layerDefinition : LayerDefinition.values()) {
            tabbedPane.add(new TileBuildTab(layerDefinition));
        }
        tabbedPane.switchTab(2); // World Objects (layer definition)

        // Add Tables...
        add(toolsTable).growX().row();
        add(layerFloorWrapperTable).growX().row();
        add(tabbedTableContainer).grow();

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        pack();
        setSize(360, 600);
        findPosition();
        setVisible(true);
        stopWindowClickThrough();
        return this;
    }

    private void activateOtherButton() {
        for (VisImageButton visImageButton : editorButtonList) {
            visImageButton.setDisabled(false);
            visImageButton.setColor(Color.WHITE);
        }

        // Clear the selected texture
        worldBuilder.setCurrentTextureId(null);

        // Auto default this to false.
        // Inside the listener, change to true when activated
        worldBuilder.setUseEraser(false);
        worldBuilder.setUseWangTile(false);
        regionManager.setEditRegion(false);
        regionManager.setDrawRegion(false);
    }

    private void activateTool(Tools tool) {
        activateOtherButton();

        switch (tool) {
            case DRAWL:
                drawlButton.setDisabled(true);
                drawlButton.setColor(Color.RED);
                break;
            case ERASE:
                worldBuilder.setUseEraser(true);
                eraserButton.setDisabled(true);
                eraserButton.setColor(Color.RED);
                break;
            case WANG:
                worldBuilder.setUseWangTile(true);
                wangBrushButton.setDisabled(true);
                wangBrushButton.setColor(Color.RED);
                break;
            case REGION_SELECT:
                regionManager.setEditRegion(true);
                regionManager.setDrawRegion(true);
                regionSelectButton.setDisabled(true);
                regionSelectButton.setColor(Color.RED);
                break;
        }
    }

    private void findPosition() {
        setPosition(Gdx.graphics.getWidth() - getWidth(), Gdx.graphics.getHeight() - getHeight());
    }

    private void resetButtons() {
        for (VisTextButton visTextButton : layerButtonMap.values()) {
            visTextButton.setDisabled(false);
        }
    }

    public void setSelectedLayerButton(LayerDefinition layerDefinition) {
        resetButtons();
        layerButtonMap.get(layerDefinition).setDisabled(true);
    }

    @Getter
    private class TileBuildTab extends Tab {

        private final LayerDefinition layerDefinition;
        private final String title;
        private final Table contentTable;
        private VisScrollPane scrollPane;

        TileBuildTab(LayerDefinition layerDefinition) {
            super(false, false);
            this.layerDefinition = layerDefinition;
            this.title = " " + layerDefinition.toString() + " ";
            contentTable = new VisTable(true);
            build();
        }

        public void build() {
            // Create a scroll pane.
            final VisTable buttonTable = new VisTable();

            // Get layer specific tags
            Tags[] tags = Tags.getLayerSpecificTags(layerDefinition, true);

            final VisCheckBox sortByTag = new VisCheckBox("Sort By Tag");
            sortByTag.setChecked(false);
            if (tags.length > 0) contentTable.add(sortByTag);

            final VisSelectBox<Tags> showTag = new VisSelectBox<>();
            showTag.setItems(tags);
            showTag.setDisabled(!sortByTag.isChecked());
            if (tags.length > 0) contentTable.add(showTag).row();

            sortByTag.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    buildImageList(buttonTable, sortByTag.isChecked(), showTag.getSelected());

                    showTag.setDisabled(!sortByTag.isChecked());
                }
            });
            showTag.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    buildImageList(buttonTable, sortByTag.isChecked(), showTag.getSelected());
                }
            });


            scrollPane = new VisScrollPane(buttonTable);
            scrollPane.setOverscroll(false, false);
            scrollPane.setFlickScroll(false);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollbarsOnTop(true);
            scrollPane.setScrollingDisabled(true, false);
            contentTable.add(scrollPane).prefHeight(1).grow().colspan(2);

            buildImageList(buttonTable, sortByTag.isChecked(), showTag.getSelected());
            // TODO: Possibly add tool-tips to each one that describes it's properties?
        }

        private void buildImageList(VisTable buttonTable, boolean sortByTag, Tags tag) {
            buttonTable.clear();

            // Add image buttons that represent the item. Filter by layer definition.
            int tilesAdded = 0;
            VisTable moduloTable = null;
            for (final TileImage tileImage : worldBuilder.getTileImageMap().values()) {

                // Sort by tag
                if (sortByTag) if (!tileImage.containsTag(tag)) continue;

                // If the TileImage is a wang tile, only show one image of it
                boolean isWangTile = false;
                if (tileImage.getFileName().startsWith(WangType.TYPE_16.getPrefix())) {
                    isWangTile = true;
                    if (!tileImage.getFileName().endsWith(WangType.TYPE_16.getDefaultWangTileImageId()))
                        continue;
                }
                if (tileImage.getFileName().startsWith(WangType.TYPE_48.getPrefix())) {
                    isWangTile = true;
                    if (!tileImage.getFileName().endsWith(WangType.TYPE_48.getDefaultWangTileImageId()))
                        continue; // Corner piece "L"
                }

                // Manually skip and ignore these images
                if (tileImage.getFileName().contains("-transition"))
                    continue; // Door animation frames
                if (tileImage.getFileName().contains("-open")) continue; // Door animation frames

                if (tileImage.getLayerDefinition() != layerDefinition) continue;
                if (tilesAdded % 8 == 0) {
                    // Create a new table every X amount of images added for scrolling purposes
                    moduloTable = new VisTable();
                    buttonTable.add(moduloTable).align(Alignment.LEFT.getAlignment()).row();
                }
                tilesAdded++;
                VisImageButton visImageButton = new VisImageButton(new ImageBuilder(clientMain, GameAtlas.TILES, tileImage.getFileName()).setSize(32).buildTextureRegionDrawable());
                if (isWangTile) visImageButton.setColor(Color.PINK);
                moduloTable.add(visImageButton);

                visImageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // See if this TileImage is a WangTile
                        if (tileImage.containsProperty(TilePropertyTypes.WANG_TILE)) {
                            activateTool(Tools.WANG);

                            WangTileProperty wangTileProperty = (WangTileProperty) tileImage.getProperty(TilePropertyTypes.WANG_TILE);
                            worldBuilder.setCurrentWangId(wangTileProperty);

                            println(TileBuildMenu.class, "Setting Wang for TileImage: " + tileImage.getFileName(), false, PRINT_DEBUG);
                            wangTileProperty.printDebug(TileBuildMenu.class);
                        } else {
                            activateTool(Tools.DRAWL);
                        }

                        // Set working layer and texture id
                        worldBuilder.setCurrentLayer(layerDefinition);
                        worldBuilder.setCurrentTextureId(tileImage.getImageId());
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

    enum Tools {
        DRAWL,
        ERASE,
        WANG,
        REGION_SELECT
    }
}
