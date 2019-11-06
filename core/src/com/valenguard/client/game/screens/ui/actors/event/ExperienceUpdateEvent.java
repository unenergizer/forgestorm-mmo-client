package com.valenguard.client.game.screens.ui.actors.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.valenguard.client.game.rpg.SkillOpcodes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExperienceUpdateEvent extends Event {
    private SkillOpcodes skillOpcode;
    private int level;
}
