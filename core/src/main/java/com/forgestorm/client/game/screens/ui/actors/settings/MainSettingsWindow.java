package com.forgestorm.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;

import lombok.Getter;

@Getter
public class MainSettingsWindow extends HideableVisWindow implements Buildable {

    private final WelcomeTab welcomeTab;
    private GameMechanicsTab gameMechanicsTab;
    private GraphicsTab graphicsTab;
    private AudioTab audioTab = new AudioTab();
    private TestTab controlsTab = new TestTab("Controls");
    private TestTab socialTab = new TestTab("Social");

    public MainSettingsWindow(StageHandler stageHandler) {
        super("Client Settings");

        this.welcomeTab = new WelcomeTab();
        this.gameMechanicsTab = new GameMechanicsTab(stageHandler);
        this.graphicsTab = new GraphicsTab(stageHandler);
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

        tabbedPane.add(welcomeTab);
        tabbedPane.add(gameMechanicsTab);
        tabbedPane.add(graphicsTab);
        tabbedPane.add(audioTab);
        tabbedPane.add(controlsTab);
        tabbedPane.add(socialTab);
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

//		debugAll();
        setSize(380, 200);
        centerWindow();
        setVisible(false);
        return this;
    }

    private class TestTab extends Tab {

        private final String title;
        private final Table content;

        TestTab(String title) {
            super(false, false);
            this.title = " " + title + " ";
            content = new VisTable();
            content.add(new VisLabel(title + ": Coming soon!"));
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public Table getContentTable() {
            return content;
        }
    }
}