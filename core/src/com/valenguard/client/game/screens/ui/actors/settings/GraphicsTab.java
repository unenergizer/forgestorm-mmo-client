package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisRadioButton;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenResolutions;
import com.valenguard.client.game.screens.WindowManager;
import com.valenguard.client.game.screens.ui.actors.WindowModes;

public class GraphicsTab extends Tab {

    private String title;
    private Table content;

    GraphicsTab() {
        super(false, false);
        title = "Graphics";
        build();
    }

    private void build() {
        content = new VisTable(true);
        final WindowManager windowManager = Valenguard.getInstance().getWindowManager();

        /*
         * Vysnc Toggle
         */
        final VisRadioButton vSyncRadioButton = new VisRadioButton("Toggle VSync");
        vSyncRadioButton.setChecked(windowManager.isUseVSync());

        content.add(vSyncRadioButton).left();

        vSyncRadioButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                windowManager.setUseVSync(!windowManager.isUseVSync());
                event.handle();
            }
        });

        /*
         * Screen Resolution
         */
        final VisSelectBox<ScreenResolutions> screenResolutionSelect = new VisSelectBox<ScreenResolutions>();
        screenResolutionSelect.setItems(ScreenResolutions.values());
        screenResolutionSelect.setSelected(windowManager.getCurrentWindowResolution());

        content.row();
        content.add(new VisLabel("Screen Resolutions")).padRight(3);
        content.add(screenResolutionSelect).left();

        screenResolutionSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                windowManager.setWindowMode(WindowModes.WINDOW, screenResolutionSelect.getSelected());
                event.handle();
            }
        });

        /*
         * Window Mode
         */
        final VisSelectBox<WindowModes> windowModesSelect = new VisSelectBox<WindowModes>();
        windowModesSelect.setItems(WindowModes.values());
        windowModesSelect.setSelected(windowManager.getCurrentWindowMode());

        content.row();
        content.add(new VisLabel("Window Mode")).padRight(3);
        content.add(windowModesSelect).left();

        windowModesSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                windowManager.setWindowMode(windowModesSelect.getSelected());
                event.handle();
            }
        });
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
