package com.valenguard.client.game.screens.ui.actors.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.valenguard.client.game.rpg.Attributes;

public abstract class StatsUpdateListener implements EventListener {

    @Override
    public boolean handle(Event event) {
        if (!(event instanceof StatsUpdateEvent)) return false;
        StatsUpdateEvent statsUpdateEvent = (StatsUpdateEvent) event;
        updateStats(statsUpdateEvent.getPlayerClientAttributes());
        return true;
    }

    protected abstract void updateStats(Attributes playerClientAttributes);
}

