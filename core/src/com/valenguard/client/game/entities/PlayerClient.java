package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.rpg.SkillOpcodes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerClient extends Player {

    private boolean isWarping = false;

    /**
     * The direction the entity intends to move in the future.
     */
    private MoveDirection predictedMoveDirection = MoveDirection.NONE;

    private final GlyphLayout regularLevelUpNumber = new GlyphLayout();
    private final GlyphLayout shadowLevelUpNumber = new GlyphLayout();
    private float distanceMoved = 0;

    @Setter
    private boolean showLevelUpMessage = false;

    public void drawLevelUpMessage() {
        if (!showLevelUpMessage) return;
        float x = getDrawX() + 8;
        float y = getDrawY() + 18 + distanceMoved;

        BitmapFont font = Valenguard.gameScreen.getFont();
        String level = "Level " + Byte.toString(Valenguard.getInstance().getSkills().getSkill(SkillOpcodes.MELEE).getLevel());

        font.getData().setScale(1f);
        font.setColor(Color.BLACK);
        shadowLevelUpNumber.setText(font, level);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), shadowLevelUpNumber, x - (shadowLevelUpNumber.width / 2) + .3f, y - .3f);

        font.getData().setScale(1f);
        font.setColor(Color.YELLOW);
        regularLevelUpNumber.setText(font, level);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), regularLevelUpNumber, x - (regularLevelUpNumber.width / 2), y);

        distanceMoved = distanceMoved + 0.11f;
        if (distanceMoved >= 9) {
            distanceMoved = 0;
            showLevelUpMessage = false;
        }
    }
}
