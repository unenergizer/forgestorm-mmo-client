package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.type.GameAtlas;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import lombok.Getter;

@Getter
public class TileBuildMenu extends HideableVisWindow implements Buildable {

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();

    public TileBuildMenu() {
        super("World Build Menu");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(true);

        final VisTable mainTable = new VisTable();
        mainTable.pad(3);

//        TabbedPane.TabbedPaneStyle style = VisUI.getSkin().get("default", TabbedPane.TabbedPaneStyle.class);
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                mainTable.clearChildren();
                mainTable.add(tab.getContentTable()).expand().fill();
            }
        });

        add(tabbedPane.getTable()).expandX().fillX();
        row();
        add(mainTable).expand().fill();

        // Add Build Categories
        for (BuildCategory buildCategory : BuildCategory.values()) {
            tabbedPane.add(new TileBuildTab(buildCategory));
        }
        tabbedPane.switchTab(0);

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

        setSize(360, 200);
        centerWindow();
        setVisible(true);
        return this;
    }

    @Getter
    private class TileBuildTab extends Tab {

        private final BuildCategory buildCategory;
        private final String title;
        private final Table contentTable;

        TileBuildTab(BuildCategory buildCategory) {
            super(false, false);
            this.buildCategory = buildCategory;
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
