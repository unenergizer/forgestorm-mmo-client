package com.valenguard.client.game.screens.ui.actors.character;

import com.valenguard.client.game.world.entities.Appearance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCharacter {
    private final String name;
    private final byte characterId;
    private final Appearance appearance;
}
