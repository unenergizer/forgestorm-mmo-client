package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.Gdx;
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
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.type.GameAtlas;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class TileBuildMenu extends HideableVisWindow implements Buildable {

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
    private final Map<LayerDefinition, VisTextButton> layerButtonMap = new HashMap<LayerDefinition, VisTextButton>();

    public TileBuildMenu() {
        super("World Build Menu");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(true);

        final Drawable drawl = new ImageBuilder().setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_pencil").setSize(32).buildTextureRegionDrawable();
        final Drawable eraser = new ImageBuilder().setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_eraser").setSize(32).buildTextureRegionDrawable();
        final Drawable wangBrush = new ImageBuilder().setGameAtlas(GameAtlas.TOOLS).setRegionName("tool_wang").setSize(32).buildTextureRegionDrawable();
        final Drawable drawableActive = new ImageBuilder().setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_158").buildTextureRegionDrawable();
        final Drawable drawableInactive = new ImageBuilder().setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_159").buildTextureRegionDrawable();

        // BUILD TOOLS
        final VisTable toolsTable = new VisTable(true);
        final VisImageButton drawlButton = new VisImageButton(drawl, "Drawl Tool");
        final VisImageButton eraserButton = new VisImageButton(eraser, "Eraser Tool");
        final VisImageButton wangBrushButton = new VisImageButton(wangBrush, "Wang Brush");

        drawlButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawlButton.setDisabled(true);
                eraserButton.setDisabled(false);
                worldBuilder.setUseEraser(false);
                wangBrushButton.setDisabled(false);
            }
        });

        eraserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawlButton.setDisabled(false);
                eraserButton.setDisabled(true);
                worldBuilder.setUseEraser(true);
                wangBrushButton.setDisabled(false);
            }
        });

        wangBrushButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawlButton.setDisabled(false);
                eraserButton.setDisabled(false);
                worldBuilder.setUseEraser(false);
                wangBrushButton.setDisabled(true);
            }
        });

        VisTable buttonTable = new VisTable();
        buttonTable.add(drawlButton);
        buttonTable.add(eraserButton);
        buttonTable.add(wangBrushButton);

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
            final Drawable layerInfo = new ImageBuilder().setGameAtlas(GameAtlas.ITEMS).setRegionName("quest_10" + i).buildTextureRegionDrawable();
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
        TabbedPane tabbedPane = new TabbedPane();
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

        // Add Tabs (Build Categories)
        for (BuildCategory buildCategory : BuildCategory.values()) {
            tabbedPane.add(new TileBuildTab(buildCategory, buildCategory.layerDefinition));
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

        private final BuildCategory buildCategory;
        private final LayerDefinition layerDefinition;
        private final String title;
        private final Table contentTable;

        TileBuildTab(BuildCategory buildCategory, LayerDefinition layerDefinition) {
            super(false, false);
            this.buildCategory = buildCategory;
            this.layerDefinition = layerDefinition;
            this.title = " " + buildCategory.toString() + " ";
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
            contentTable.add(scrollPane).prefHeight(1).grow();

            // Add image buttons that represent the item. Filter by category.
            int tilesAdded = 0;
            VisTable moduloTable = null;
            for (final TileImage tileImage : worldBuilder.getTileImageMap().values()) {
                if (tileImage.getBuildCategory() != buildCategory) continue;
                if (tilesAdded % 8 == 0) {
                    // Create a new table every X amount of images added for scrolling purposes
                    moduloTable = new VisTable();
                    buttonTable.add(moduloTable).align(Alignment.LEFT.getAlignment()).row();
                }
                tilesAdded++;
                VisImageButton visImageButton = new VisImageButton(new ImageBuilder(GameAtlas.TILES, tileImage.getFileName()).setSize(32).buildTextureRegionDrawable());
                moduloTable.add(visImageButton);

                visImageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
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
}
