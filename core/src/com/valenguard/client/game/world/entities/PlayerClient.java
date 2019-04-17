package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.FocusManager;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.SkillOpcodes;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.valenguard.client.game.world.item.BankActions;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.network.game.packet.out.BankManagePacketOut;
import com.valenguard.client.util.GameTextUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerClient extends Player {

    private boolean isWarping = false;

    private boolean isBankOpen = false;

    /**
     * The direction the entity intends to move in the future.
     */
    private MoveDirection predictedMoveDirection = MoveDirection.NONE;

    private MovingEntity targetEntity;

    @Setter
    private boolean showLevelUpMessage = false;
    private float distanceMoved = 0;

    public void closeBankWindow() {
        if (!isBankOpen) return;
        ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getBankWindow());
        isBankOpen = false;
        new BankManagePacketOut(BankActions.PLAYER_REQUEST_CLOSE).sendPacket();
    }

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
