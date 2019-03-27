package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.SkillOpcodes;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.util.GameTextUtil;

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

    @Setter
    private boolean showLevelUpMessage = false;
    private float distanceMoved = 0;

    public void drawLevelUpMessage() {
        if (!showLevelUpMessage) return;
        float x = getDrawX() + 8;
        float y = getDrawY() + 18 + distanceMoved;
        String level = "Level " + Byte.toString(Valenguard.getInstance().getSkills().getSkill(SkillOpcodes.MELEE).getLevel());

        GameTextUtil.drawMessage(level, Color.YELLOW, 1f, x, y);

        distanceMoved = distanceMoved + 0.11f;
        if (distanceMoved >= 9) {
            distanceMoved = 0;
            showLevelUpMessage = false;
        }
    }
}
