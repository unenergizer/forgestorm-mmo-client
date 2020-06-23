package com.forgestorm.client.game.screens.ui.actors.character;

import com.forgestorm.client.game.world.entities.Appearance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCharacter {
    private final String name;
    private final byte characterId;
    private final Appearance appearance;
}
