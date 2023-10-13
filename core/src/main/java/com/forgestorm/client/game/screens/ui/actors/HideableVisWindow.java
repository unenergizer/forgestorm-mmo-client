package com.forgestorm.client.game.screens.ui.actors;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.character.CharacterCreation;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class HideableVisWindow extends VisWindow {

    private final ClientMain clientMain;
    private boolean fadeOutActionRunning;

    public HideableVisWindow(ClientMain clientMain, String title) {
        super(title);
        this.clientMain = clientMain;
    }

    public HideableVisWindow(ClientMain clientMain, String title, String styleName) {
        super(title, styleName);
        this.clientMain = clientMain;
    }

    /**
     * Fade outs this window, when fade out animation is completed, window is removed from Stage. Calling this for the
     * second time won't have any effect if previous animation is still running.
     */
    @Override
    public void fadeOut(float time) {
        if (fadeOutActionRunning) return;
        fadeOutActionRunning = true;
        final Touchable previousTouchable = getTouchable();
        setTouchable(Touchable.disabled);
        Stage stage = getStage();
        if (stage != null && stage.getKeyboardFocus() != null && stage.getKeyboardFocus().isDescendantOf(this)) {
            FocusManager.resetFocus(stage);
        }
        addAction(Actions.sequence(Actions.fadeOut(time, Interpolation.fade), new Action() {
            @Override
            public boolean act(float delta) {
                setTouchable(previousTouchable);
//                remove(); // replaced with setVisible(false) instead
                setVisible(false); // replaces remove();
                getColor().a = 1f;
                fadeOutActionRunning = false;
                return true;
            }
        }));
    }

    public void stopWindowClickThrough() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true; // Handle window click through
            }
        });
    }

    /**
     * Overriding the default addCloseButton() method to fade the window out when it
     * is closed.
     */
    @Override
    public void addCloseButton() {
        addCloseButton(null);
    }

    public void addCloseButton(final CloseButtonCallBack closeButtonCallBack) {
        Label titleLabel = getTitleLabel();
        Table titleTable = getTitleTable();

        VisImageButton closeButton = new VisImageButton("close-window");
        titleTable.add(closeButton).padRight(-getPadRight() + 0.7f);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fadeOut(FADE_TIME);
                if (closeButtonCallBack != null) closeButtonCallBack.closeButtonClicked();
                clientMain.getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 15);

                // Clear scroll focus so map zooming can resume.
                clientMain.getStageHandler().getStage().setScrollFocus(null);
            }
        });
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
                return true;
            }
        });

        if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
    }

    public abstract static class CloseButtonCallBack {
        public abstract void closeButtonClicked();
    }
}
