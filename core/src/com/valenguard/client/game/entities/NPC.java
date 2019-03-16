package com.valenguard.client.game.entities;

import com.valenguard.client.game.rpg.FactionTypes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NPC extends AiEntity {
    private FactionTypes faction;
}
