package com.forgestorm.client.io;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FilePaths {

    // Root
    NETWORK_SETTINGS("Network.yaml"),

    // Abilities
    COMBAT_ABILITIES("abilities/CombatAbilities.yaml"),

    // Entity
    FACTIONS("entity/Factions.yaml"),

    // Item
    ENTITY_SHOP("item/ShopItems.yaml"),
    ITEM_STACK("item/ItemStacks.yaml"),

    // Language
    LANG_ENG("language/english.yaml"),

    // Maps
    MAPS("maps"),
    TILES("graphics/TileProperties.yaml"),

    // Sounds
    SOUND_FX("sound/SoundFX.yaml"),
    GAME_MUSIC("sound/GameMusic.yaml");

    private String filePath;

    public String getFilePath() {
        return "data/" + filePath;
    }
}
