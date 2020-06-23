package com.forgestorm.client.game.screens.ui.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

class RainbowAction extends TemporalAction {
    private final Color[] colours;

    public RainbowAction(float duration, Color... colours) {
        setDuration(duration);
        this.colours = colours;
    }

    @Override
    protected void update(float percent) {
//        actor.setColor(Colours.shiftedTowards(colours, percent));
    }
}
