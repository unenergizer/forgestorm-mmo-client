package com.valenguard.client.game.screens.ui.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class RainbowAction extends TemporalAction {
    Color[] colours;

    public RainbowAction(float duration, Color... colours) {
        setDuration(duration);
        this.colours = colours;
    }

    @Override
    protected void update(float percent) {
//        actor.setColor(Colours.shiftedTowards(colours, percent));
    }
}
