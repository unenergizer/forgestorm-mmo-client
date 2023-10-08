package com.forgestorm.client.game.screens.ui.actors.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.forgestorm.shared.game.rpg.Attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsUpdateEvent extends Event {
    private final Attributes playerClientAttributes;
}
