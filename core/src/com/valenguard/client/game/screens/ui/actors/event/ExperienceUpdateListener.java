package com.valenguard.client.game.screens.ui.actors.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.valenguard.client.game.rpg.SkillOpcodes;

public abstract class ExperienceUpdateListener implements EventListener {

    @Override
    public boolean handle(Event event) {
        if (!(event instanceof ExperienceUpdateEvent)) return false;
        ExperienceUpdateEvent experienceUpdateEvent = (ExperienceUpdateEvent) event;
        updateLevel(experienceUpdateEvent.getSkillOpcode(), experienceUpdateEvent.getLevel());
        return true;
    }

    protected abstract void updateLevel(SkillOpcodes skillOpcode, int level);
}

