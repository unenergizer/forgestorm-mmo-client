package com.forgestorm.client.game;

import com.badlogic.gdx.graphics.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameTextures {
    public Texture entityShadow;

    public void dispose() {
        entityShadow.dispose();
    }
}
