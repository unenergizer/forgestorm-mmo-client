package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.ClickAction;
import com.forgestorm.client.game.movement.AbstractPostProcessor;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.game.EntityDropDownMenu;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.screens.ui.actors.game.paging.EntityShopWindow;
import com.forgestorm.client.game.world.item.BankActions;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.game.world.item.trade.TradePacketInfoOut;
import com.forgestorm.client.game.world.item.trade.TradeStatusOpcode;
import com.forgestorm.client.network.game.packet.out.BankManagePacketOut;
import com.forgestorm.client.network.game.packet.out.ClickActionPacketOut;
import com.forgestorm.client.network.game.packet.out.NPCDialoguePacketOut;
import com.forgestorm.client.network.game.packet.out.PlayerTradePacketOut;

public class EntityInteract {

    public static void pickUpItemStackDrop(final ItemStackDrop itemStackDrop) {
        interact(itemStackDrop, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                ItemStack itemStack = ClientMain.getInstance().getItemStackManager().makeItemStack(itemStackDrop.getItemStackId(), 1);
                ClientMain.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), itemStack);
                new ClickActionPacketOut(new ClickAction(ClickAction.LEFT, itemStackDrop)).sendPacket();
            }
        }, (short) 1);
    }

    public static void openBank(MovingEntity entity) {
        interact(entity, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                new BankManagePacketOut(BankActions.PLAYER_REQUEST_OPEN).sendPacket();
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    public static void talkNPC(final NPC npc) {
        interact(npc, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                new NPCDialoguePacketOut(npc).sendPacket();
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    public static void openShop(final AiEntity entity) {
        interact(entity, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                final EntityShopWindow pagedItemStackWindow = ClientMain.getInstance().getStageHandler().getPagedItemStackWindow();
                pagedItemStackWindow.openWindow(entity, entity.getShopID());
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    public static void trade(final StageHandler stageHandler, final Player playerToTradeWith) {
        interact(playerToTradeWith, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_INIT_TARGET, playerToTradeWith.getServerEntityID())).sendPacket();
                stageHandler.getTradeWindow().setTradeTarget(playerToTradeWith);
                stageHandler.getChatWindow().appendChatMessage(ChatChannelType.TRADE, "[Client] Sending trade request...");
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    private static void interact(Entity entity, AbstractPostProcessor postProcessor, short distanceCheck) {
        ClientMain.getInstance().getEntityTracker().walkTo(entity);
        ClientMain.getInstance().getEntityTracker().setDistanceCheck(distanceCheck);
        ClientMain.getInstance().getEntityTracker().setPostProcessor(postProcessor);
    }

}
