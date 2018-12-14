package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

import lombok.Getter;

@Getter
public class MainSettingsWindow extends HideableVisWindow implements Buildable, Focusable {

    private WelcomeTab welcomeTab = new WelcomeTab();
    private TestTab gameMechanicsTab = new TestTab("Game Mechanics");
    private GraphicsTab graphicsTab = new GraphicsTab();
    private AudioTab audioTab = new AudioTab();
    private TestTab controlsTab = new TestTab("Controls");
    private TestTab socialTab = new TestTab("Social");

    public MainSettingsWindow() {
        super("Client Settings");
    }

    @Override
    public Actor build() {
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

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

//		debugAll();
        setSize(380, 200);
        centerWindow();
        setVisible(false);
        return this;
    }

    @Override
    public void focusLost() {
    }

    @Override
    public void focusGained() {
    }

    private class TestTab extends Tab {

        private String title;
        private Table content;

        TestTab(String title) {
            super(false, false);
            this.title = " " + title + " ";
            content = new VisTable();
            content.add(new VisLabel(title + ": Coming soon!"));
        }

        TestTab(String title, boolean closeableByUser) {
            super(false, closeableByUser);
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
