package com.valenguard.client.io;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FilePaths {

    // Root
    NETWORK_SETTINGS("Network.yaml"),

    // Abilities
    COMBAT_ABILITIES("abilities/CombatAbilities.yaml"),

    // Item
    ENTITY_SHOP("item/ShopItems.yaml"),
    ITEM_STACK("item/ItemStacks.yaml"),

    // Maps
    MAPS("maps"),

    // Sounds
    SOUND_FX("sound/SoundFX.yaml"),
    GAME_MUSIC("sound/GameMusic.yaml");

    private String filePath;

    public String getFilePath() {
        return "data/" + filePath;
    }
}
