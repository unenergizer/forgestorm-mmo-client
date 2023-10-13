package com.forgestorm.client.game.world.entities;

import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.SkillOpcodes;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.world.item.BankActions;
import com.forgestorm.client.network.game.packet.out.BankManagePacketOut;
import com.forgestorm.client.util.GameTextUtil;
import com.forgestorm.shared.game.world.maps.MoveDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerClient extends Player {

    private final ClientMain clientMain;
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

    public PlayerClient(ClientMain clientMain) {
        super(clientMain);
        this.clientMain = clientMain;
    }

    public void closeBankWindow() {
        if (!isBankOpen) return;
        getClientMain().getStageHandler().getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[RED]Bank window closed because you moved.");
        ActorUtil.fadeOutWindow(getClientMain().getStageHandler().getBankWindow());
        isBankOpen = false;
        new BankManagePacketOut(clientMain, BankActions.PLAYER_REQUEST_CLOSE).sendPacket();
    }

    public void drawLevelUpMessage() {
        if (!showLevelUpMessage) return;
        float x = getDrawX() + 8;
        float y = getDrawY() + 18 + distanceMoved;
        String level = "Level " + getClientMain().getSkills().getSkill(SkillOpcodes.MELEE).getCurrentLevel();

        GameTextUtil.drawMessage(clientMain, level, Color.YELLOW, 1f, x, y);

        distanceMoved = distanceMoved + 0.11f;
        if (distanceMoved >= 9) {
            distanceMoved = 0;
            showLevelUpMessage = false;
        }
    }

    public void setTargetEntity(MovingEntity movingEntity) {
        StageHandler stageHandler = getClientMain().getStageHandler();
        if (targetEntity == movingEntity || movingEntity == null) {
            stageHandler.getChatWindow().appendChatMessage(ChatChannelType.COMBAT, "[YELLOW]No longer targeting " + targetEntity.getEntityName() + ".");
            stageHandler.getTargetStatusBar().setVisible(false);
        }
        if (targetEntity == movingEntity) movingEntity = null;
        if (targetEntity == this) return; // Do not target self
        if (targetEntity != null) {
            // remove name highlight
            targetEntity.setPlayerClientTarget(false);
        }

        targetEntity = movingEntity;

        stageHandler.getTargetStatusBar().initTarget(movingEntity);

        // TODO: Remove/Redo old GameButtonBar abilities setup
//        if (targetEntity == null) {
//            stageHandler.getGameButtonBar().canUseAbilities(false);
//        } else {
//            targetEntity.setPlayerClientTarget(true);
//            stageHandler.getChatWindow().appendChatMessage("[YELLOW]You targeted " + targetEntity.getEntityName() + ".");
//            stageHandler.getGameButtonBar().canUseAbilities(true);
//        }
    }
}
