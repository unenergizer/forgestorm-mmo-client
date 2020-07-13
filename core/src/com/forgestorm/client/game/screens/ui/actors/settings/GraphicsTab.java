package com.forgestorm.client.game.screens.ui.actors.settings;

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
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.WindowManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.constant.ScreenResolutions;
import com.forgestorm.client.game.screens.ui.actors.constant.WindowModes;

import static com.forgestorm.client.util.ApplicationUtil.userOnMobile;

public class GraphicsTab extends Tab {

    private final StageHandler stageHandler;
    private final VisSlider slider = new VisSlider(ClientConstants.ZOOM_LIMIT_IN, ClientConstants.ZOOM_LIMIT_OUT, ClientConstants.ZOOM_CHANGE, false);
    private final VisCheckBox vSyncCheckBox = new VisCheckBox("");
    private final VisSelectBox<ScreenResolutions> screenResolutionSelect = new VisSelectBox<ScreenResolutions>();
    private final VisSelectBox<WindowModes> windowModesSelect = new VisSelectBox<WindowModes>();
    private final String title;
    private Table content;

    GraphicsTab(StageHandler stageHandler) {
        super(false, false);
        this.stageHandler = stageHandler;
        title = " Graphics ";
        build();
    }

    private void build() {
        content = new VisTable(true);
        final WindowManager windowManager = ClientMain.getInstance().getWindowManager();

        /*
         * Zoom Level
         */
        slider.setValue(ClientConstants.ZOOM_DEFAULT);

        content.add(new VisLabel("Zoom Level")).padRight(3);
        content.add(slider).left();

        slider.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (ClientMain.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) {
                    Dialogs.showOKDialog(stageHandler.getStage(), "Error!", "Option can only be set in-game.");
                    return true;
                }
                return false;
            }
        });

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ClientMain.getInstance().getUserInterfaceType() == UserInterfaceType.GAME) {
                    ClientMain.getInstance().getGameScreen().getCamera().changeZoomLevel(slider.getValue());
                } else {
                    event.cancel();
                }
                event.handle();
            }
        });

        /*
         * VSync Toggle
         */
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

    public void setWindowMode(WindowModes windowMode) {
        windowModesSelect.setSelected(windowMode);
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
