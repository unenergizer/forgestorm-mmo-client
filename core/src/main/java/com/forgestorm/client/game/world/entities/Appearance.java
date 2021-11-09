package com.forgestorm.client.game.world.entities;

import com.badlogic.gdx.graphics.Color;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Appearance {
    // Monster, ItemStackDrop, SkillNode, etc
    private byte singleBodyTexture = -1;

    // Humanoid appearance
    private byte hairTexture = -1;
    private byte helmTexture = -1;
    private byte chestTexture = -1;
    private byte pantsTexture = -1;
    private byte shoesTexture = -1;
    private Color hairColor;
    private Color eyeColor;
    private Color skinColor;
    private Color glovesColor;
    private Color borderColor = Color.BLACK;
    private byte leftHandTexture = -1;
    private byte rightHandTexture = -1;
}
