package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.LeftAlignTextButton;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.maps.Tags;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.type.GameAtlas;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class TileBuildMenu extends HideableVisWindow implements Buildable {

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
    private final Map<LayerDefinition, VisTextButton> layerButtonMap = new HashMap<LayerDefinition, VisTextButton>();
    private final List<VisImageButton> editorButtonList = new ArrayList<VisImageButton>();

    private VisImageButton drawlButton;
    private VisImageButton eraserButton;
    private VisImageButton wangBrushButton;

    private TabbedPane tabbedPane;

    public TileBuildMenu() {
        super("World Build Menu");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(true);

        final ImageBuilder imageBuilder = new ImageBuilder();
        final Drawable drawl = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_pencil").setSize(32).buildTextureRegionDrawable();
        final Drawable eraser = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_eraser").setSize(32).buildTextureRegionDrawable();
        final Drawable wangBrush = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_wang").setSize(32).buildTextureRegionDrawable();
        final Drawable runAllow = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_run_allow").setSize(32).buildTextureRegionDrawable();
        final Drawable runStop = imageBuilder.setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_run_stop").setSize(32).buildTextureRegionDrawable();
        final Drawable drawableActive = imageBuilder.setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_158").setSize(16).buildTextureRegionDrawable();
        final Drawable drawableInactive = imageBuilder.setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_159").setSize(16).buildTextureRegionDrawable();

        // BUILD TOOLS
        final VisTable toolsTable = new VisTable(true);
        drawlButton = new VisImageButton(drawl, "Drawl Tool");
        eraserButton = new VisImageButton(eraser, "Eraser Tool");
        wangBrushButton = new VisImageButton(wangBrush, "Wang Brush");
        final VisImageButton allowRunningButton = new VisImageButton(runAllow, "Allow/Prevent Click to Move");

        drawlButton.setGenerateDisabledImage(true);
        eraserButton.setGenerateDisabledImage(true);
        wangBrushButton.setGenerateDisabledImage(true);
        allowRunningButton.setGenerateDisabledImage(true);

        editorButtonList.add(drawlButton);
        editorButtonList.add(eraserButton);
        editorButtonList.add(wangBrushButton);
        editorButtonList.add(allowRunningButton);

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

        VisTable buttonTable = new VisTable();
        buttonTable.add(drawlButton);
        buttonTable.add(eraserButton);
        buttonTable.add(wangBrushButton);
        buttonTable.add(allowRunningButton);

        toolsTable.add(new VisLabel("[YELLOW]Tools:")).align(Alignment.LEFT.getAlignment()).row();
        toolsTable.add(buttonTable).align(Alignment.LEFT.getAlignment()).row();
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
            LeftAlignTextButton layerSelectButton = new LeftAlignTextButton(layerDefinition.getLayerName());
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
        floorSelectTable.add(new VisLabel("[YELLOW]Floor Select:")).row();
        floorSelectTable.add(new VisLabel("Coming soon...")).row();

        // LAYER AND FLOOR WRAPPER TABLE ADD....
        final VisTable layerFloorWrapperTable = new VisTable(true);
        layerFloorWrapperTable.add(layerContainer);
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
        tabbedPane.switchTab(0);

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

        // Auto default this to false.
        // Inside the listener, change to true when activated
        worldBuilder.setUseEraser(false);
        worldBuilder.setUseWangTile(false);
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

            final VisSelectBox<Tags> showTag = new VisSelectBox<Tags>();
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
                if (tileImage.getFileName().startsWith("BW4") || tileImage.getFileName().startsWith("BW16")) {
                    isWangTile = true;
                    if (!tileImage.getFileName().endsWith("-0")) continue;
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
                VisImageButton visImageButton = new VisImageButton(new ImageBuilder(GameAtlas.TILES, tileImage.getFileName()).setSize(32).buildTextureRegionDrawable());
                if (isWangTile) visImageButton.setColor(Color.PINK);
                moduloTable.add(visImageButton);

                visImageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        worldBuilder.setCurrentLayer(layerDefinition);
                        worldBuilder.setCurrentTextureId(tileImage.getImageId());

                        // See if this TileImage is a WangTile
                        if (tileImage.containsProperty(TilePropertyTypes.WANG_TILE)) {
                            activateTool(Tools.WANG);

                            Map<Integer, WangTile> wangs = ClientMain.getInstance().getFileManager().getWangPropertiesData().getWangImageMap();
                            for (Map.Entry<Integer, WangTile> entry : wangs.entrySet()) {
                                int id = entry.getKey();
                                WangTile wangTile = entry.getValue();

                                if (tileImage.getFileName().contains(wangTile.getFileName())) {
                                    worldBuilder.setCurrentWangId(id);
                                }
                            }
                        } else {
                            activateTool(Tools.DRAWL);
                        }
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
        WANG
    }
}
