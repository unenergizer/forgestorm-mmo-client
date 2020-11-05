package com.forgestorm.client.io.type;

import lombok.AllArgsConstructor;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
@AllArgsConstructor
public enum GameAtlas {

    CURSOR("cursor.atlas"),
    ITEMS("items.atlas"),
    LOADING_SCREEN("loading.pack"),
    ENTITY_CHARACTER("character.atlas"),
    ENTITY_MONSTER("monster.atlas"),
    SKILL_NODES("skillnodes.atlas"),
    PIXEL_FX("pixelfx.atlas"),
    TILES("tiles.atlas");

    private final String filePath;

    public String getFilePath() {
        return "graphics/atlas/" + filePath;
    }
}
