package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisWindow;

public class HideableVisWindow extends VisWindow {

    private boolean fadeOutActionRunning;

    public HideableVisWindow(String title) {
        super(title);
    }

    public HideableVisWindow(String title, String styleName) {
        super(title, styleName);
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
}
