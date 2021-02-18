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
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.type.GameAtlas;
import com.kotcrab.vis.ui.util.TableUtils;
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
        final Drawable drawableActive = new ImageBuilder().setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_158").buildTextureRegionDrawable();
        final Drawable drawableInactive = new ImageBuilder().setGameAtlas(GameAtlas.ITEMS).setRegionName("skill_159").buildTextureRegionDrawable();

        // BUILD TOOLS
        final VisTable toolsTable = new VisTable(true);
        final VisImageButton drawlButton = new VisImageButton(drawl, "Drawl Tool");
        final VisImageButton eraserButton = new VisImageButton(eraser, "Eraser Tool");

        drawlButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawlButton.setDisabled(true);
                eraserButton.setDisabled(false);
                worldBuilder.setUseEraser(false);
            }
        });

        eraserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawlButton.setDisabled(false);
                eraserButton.setDisabled(true);
                worldBuilder.setUseEraser(true);
            }
        });

        toolsTable.add(new VisLabel("Tools:")).colspan(2).row();
        toolsTable.add(drawlButton);
        toolsTable.add(eraserButton);

        // LAYER SELECT
        final VisTable layerSelectTable = new VisTable();
        layerSelectTable.add(new VisLabel("Layer Select:")).colspan(2).row();
        for (final LayerDefinition layerDefinition : LayerDefinition.values()) {
            // Layer select button
            VisTextButton layerSelectButton = new VisTextButton(layerDefinition.getLayerName());
            layerSelectTable.add(layerSelectButton);
            layerSelectButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    worldBuilder.setCurrentLayer(layerDefinition);
                    resetButtons();
                    setSelectedLayerButton(layerDefinition);
                }
            });
            layerButtonMap.put(layerDefinition, layerSelectButton);

            // Layer Visibility button
            final VisImageButton layerVisibilityButton = new VisImageButton(drawableActive, "Toggle Layer Visibility");
            layerSelectTable.add(layerVisibilityButton).row();
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
        }

        // Now get the active layer, and disable that button (to indicate it's being used).
        // WorldBuilder class is setup first and the layer is decided then. Update the UI here.
        layerButtonMap.get(worldBuilder.getCurrentLayer()).setDisabled(true);

        // FLOOR SELECT
        final VisTable floorSelectTable = new VisTable();
        floorSelectTable.add(new VisLabel("Floor Select:"));

        // TABBED TILE SELECT TABLE
        final VisTable tabbedTable = new VisTable();
        tabbedTable.pad(3);

        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                tabbedTable.clearChildren();
                tabbedTable.add(tab.getContentTable()).expand().fill();
            }
        });

        tabbedTable.add(tabbedPane.getTable()).expandX().fillX().row();

        // Add Build Categories
        for (BuildCategory buildCategory : BuildCategory.values()) {
            tabbedPane.add(new TileBuildTab(buildCategory, buildCategory.layerDefinition));
        }
        tabbedPane.switchTab(0);

        // Add Tables...
        add(toolsTable).colspan(2).row();
        add(layerSelectTable);
        add(floorSelectTable).row();
        add(tabbedTable).expand().fill().colspan(2);

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
            contentTable.add(scrollPane).growX().expandY().top();

            // Add image buttons that represent the item. Filter by category.
            int tilesAdded = 0;
            VisTable moduloTable = null;
            for (final TileImage tileImage : worldBuilder.getTileImageMap().values()) {
                if (tileImage.getBuildCategory() != buildCategory) continue;
                if (tilesAdded % 7 == 0) {
                    // Create a new table every X amount of images added for scrolling purposes
                    moduloTable = new VisTable(true);
                    buttonTable.add(moduloTable).row();
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
