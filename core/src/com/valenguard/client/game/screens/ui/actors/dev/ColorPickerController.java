package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;

import lombok.Getter;

public class ColorPickerController extends Actor implements Buildable, Disposable {

    private final ColorPicker colorPicker = new ColorPicker();

    private StageHandler stageHandler;
    @Getter
    private ColorPickerColorHandler currentColorPickerHandler;

    @Getter
    private boolean disposed = false;

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;

        colorPicker.setAllowAlphaEdit(true);
        colorPicker.setCloseAfterPickingFinished(true);
        colorPicker.setShowHexFields(true);
        colorPicker.setListener(new ColorPickerAdapter() {
            @Override
            public void canceled(Color oldColor) {
                if (currentColorPickerHandler != null) {
                    currentColorPickerHandler.doColorChange(oldColor);
                    currentColorPickerHandler.setFinishedColor(oldColor);
                }
            }

            @Override
            public void changed(Color newColor) {
                if (currentColorPickerHandler != null)
                    currentColorPickerHandler.doColorChange(newColor);
            }

            @Override
            public void reset(Color previousColor, Color newColor) {
                if (currentColorPickerHandler != null) {
                    currentColorPickerHandler.doColorChange(newColor);
                    currentColorPickerHandler.setFinishedColor(newColor);
                }
            }

            @Override
            public void finished(Color newColor) {
                if (currentColorPickerHandler != null)
                    currentColorPickerHandler.doFinishedColor(newColor);
            }
        });
        return this;
    }

    public void show(ColorPickerColorHandler currentContainer) {
        this.currentColorPickerHandler = currentContainer;
        stageHandler.getStage().addActor(colorPicker.fadeIn());
        colorPicker.toFront();
        colorPicker.setColor(currentContainer.getFinishedColor());
    }

    @Override
    public void dispose() {
        disposed = true;
        colorPicker.dispose();
    }
}
