package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.ClickAction;
import com.forgestorm.client.game.movement.AbstractPostProcessor;
import com.forgestorm.client.game.rpg.ShopOpcodes;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.game.EntityDropDownMenu;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.screens.ui.actors.game.paging.EntityShopWindow;
import com.forgestorm.client.game.world.item.BankActions;
import com.forgestorm.client.game.world.item.trade.TradePacketInfoOut;
import com.forgestorm.client.network.game.packet.out.*;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.trade.TradeStatusOpcode;

public class EntityInteract {

    public static void pickUpItemStackDrop(ClientMain clientMain, final ItemStackDrop itemStackDrop) {
        interact(clientMain, itemStackDrop, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                ItemStack itemStack = clientMain.getItemStackManager().makeItemStack(itemStackDrop.getItemStackId(), 1);
                clientMain.getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), itemStack);
                new ClickActionPacketOut(clientMain, new ClickAction(ClickAction.LEFT, itemStackDrop)).sendPacket();
            }
        }, (short) 1);
    }

    public static void openBank(ClientMain clientMain, MovingEntity entity) {
        interact(clientMain, entity, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                clientMain.getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                new BankManagePacketOut(clientMain, BankActions.PLAYER_REQUEST_OPEN).sendPacket();
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    public static void talkNPC(ClientMain clientMain, final NPC npc) {
        interact(clientMain, npc, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                clientMain.getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                new NPCDialoguePacketOut(clientMain, npc).sendPacket();
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    public static void openShop(ClientMain clientMain, final AiEntity entity) {
        interact(clientMain, entity, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                clientMain.getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                new EntityShopPacketOut(clientMain, ShopOpcodes.START_SHOPPING, entity.getServerEntityID()).sendPacket();
                final EntityShopWindow pagedItemStackWindow = clientMain.getStageHandler().getPagedItemStackWindow();
                pagedItemStackWindow.openWindow(entity, entity.getShopID());
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    public static void trade(ClientMain clientMain, final StageHandler stageHandler, final Player playerToTradeWith) {
        interact(clientMain, playerToTradeWith, new AbstractPostProcessor() {
            @Override
            public void postMoveAction() {
                clientMain.getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                new PlayerTradePacketOut(clientMain, new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_INIT_TARGET, playerToTradeWith.getServerEntityID())).sendPacket();
                stageHandler.getTradeWindow().setTradeTarget(playerToTradeWith);
                stageHandler.getChatWindow().appendChatMessage(ChatChannelType.TRADE, "[Client] Sending trade request...");
            }
        }, ClientConstants.MAX_INTERACT_DISTANCE);
    }

    private static void interact(ClientMain clientMain, Entity entity, AbstractPostProcessor postProcessor, short distanceCheck) {
        clientMain.getEntityTracker().walkTo(entity);
        clientMain.getEntityTracker().setDistanceCheck(distanceCheck);
        clientMain.getEntityTracker().setPostProcessor(postProcessor);
    }

}
