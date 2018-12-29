package com.valenguard.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.WindowManager;
import com.valenguard.client.game.screens.ui.actors.constant.ScreenResolutions;
import com.valenguard.client.game.screens.ui.actors.constant.WindowModes;

import static com.valenguard.client.util.ApplicationUtil.userOnMobile;

public class GraphicsTab extends Tab {

    private final VisSlider slider = new VisSlider(ClientConstants.ZOOM_LIMIT_IN, ClientConstants.ZOOM_LIMIT_OUT, ClientConstants.ZOOM_CHANGE, false);

    private final String title;
    private Table content;

    GraphicsTab() {
        super(false, false);
        title = " Graphics ";
        build();
    }

    private void build() {
        content = new VisTable(true);
        final WindowManager windowManager = Valenguard.getInstance().getWindowManager();

        /*
         * Zoom Level
         */
        slider.setValue(ClientConstants.ZOOM_DEFAULT);

        content.add(new VisLabel("Zoom Level")).padRight(3);
        content.add(slider).left();

        slider.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Valenguard.getInstance().getScreenType() != ScreenType.GAME) {
                    Dialogs.showOKDialog(Valenguard.getInstance().getStageHandler().getStage(), "Error!", "Option can only be set in-game.");
                    return true;
                }
                return false;
            }
        });

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
                    Valenguard.gameScreen.getCamera().changeZoomLevel(slider.getValue());
                } else {
                    event.cancel();
                }
                event.handle();
            }
        });

        /*
         * VSync Toggle
         */
        final VisCheckBox vSyncCheckBox = new VisCheckBox("");
        vSyncCheckBox.setChecked(windowManager.isUseVSync());
        vSyncCheckBox.setDisabled(userOnMobile());

        content.row();
        content.add(new VisLabel("Toggle VSync")).padRight(3);
        content.add(vSyncCheckBox).left();

        vSyncCheckBox.addListener(new ChangeListener() {
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
        screenResolutionSelect.setDisabled(userOnMobile());

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
        windowModesSelect.setDisabled(userOnMobile());

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

    public void setZoomLevel(float zoomLevel) {
        slider.setValue(zoomLevel);
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
