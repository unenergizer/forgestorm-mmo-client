package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.game.TradeWindow;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.game.world.item.trade.TradeStatusOpcode;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.PLAYER_TRADE)
public class PlayerTradePacketIn implements PacketListener<PlayerTradePacketIn.TradeRequestPacket> {

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public PlayerTradePacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        println(getClass(), "Trade request in", false, PRINT_DEBUG);

        final TradeStatusOpcode tradeStatusOpcode = TradeStatusOpcode.getTradeStatusOpcode(clientHandler.readByte());
        int tradeUUID = -1;
        short tradeTargetUUID = -2;
        short confirmedPlayerUUID = -3;
        int itemId = -4;
        int itemAmount = 1;
        byte itemSlot = -5;

        //noinspection ConstantConditions
        switch (tradeStatusOpcode) {
            case TRADE_REQUEST_INIT_SENDER:
            case TRADE_REQUEST_INIT_TARGET:
                tradeUUID = clientHandler.readInt();
                tradeTargetUUID = clientHandler.readShort();
                break;
            case TRADE_REQUEST_TARGET_ACCEPT:
            case TRADE_REQUEST_TARGET_DECLINE:
            case TRADE_REQUEST_SERVER_TIMED_OUT:
            case TRADE_OFFER_COMPLETE:
            case TRADE_CANCELED:
                tradeUUID = clientHandler.readInt();
                break;
            case TRADE_OFFER_CONFIRM:
            case TRADE_OFFER_UNCONFIRM:
                tradeUUID = clientHandler.readInt();
                confirmedPlayerUUID = clientHandler.readShort();
                break;
            case TRADE_ITEM_ADD:
                tradeUUID = clientHandler.readInt();
                itemId = clientHandler.readInt();
                itemAmount = clientHandler.readInt();
                break;
            case TRADE_ITEM_REMOVE:
                tradeUUID = clientHandler.readInt();
                itemSlot = clientHandler.readByte();
                break;
            default:
                println(getClass(), "Decode unused trade status: " + tradeStatusOpcode, true, true);
                break;
        }

        return new TradeRequestPacket(tradeStatusOpcode, tradeUUID, tradeTargetUUID, confirmedPlayerUUID, itemId, itemAmount, itemSlot);
    }

    @Override
    public void onEvent(TradeRequestPacket packetData) {
        StageHandler stageHandler = clientMain.getStageHandler();

        println(getClass(), "Opcode: " + packetData.tradeStatusOpcode, false, PRINT_DEBUG);

        switch (packetData.tradeStatusOpcode) {

            // Stage 1: Init trade
            case TRADE_REQUEST_INIT_SENDER:
                clientMain.getTradeManager().setTradeUUID(packetData.tradeUUID);
                break;
            case TRADE_REQUEST_INIT_TARGET:
                clientMain.getTradeManager().setTradeUUID(packetData.tradeUUID);
                ActorUtil.fadeInWindow(stageHandler.getIncomingTradeRequestWindow());
                stageHandler.getTradeWindow().setTradeTarget(clientMain.getEntityManager().getPlayerEntity(packetData.tradeTargetUUID));
                break;

            // Stage 2: Wait for TargetPlayer response or time out
            case TRADE_REQUEST_TARGET_ACCEPT:
                TradeWindow tradeWindow = stageHandler.getTradeWindow();
                tradeWindow.getTitleLabel().setText("Trading with " + tradeWindow.getTradeTarget().getEntityName());

                ActorUtil.fadeOutWindow(stageHandler.getEntityDropDownMenu());
                ActorUtil.fadeOutWindow(stageHandler.getItemDropDownMenu());
                tradeWindow.openWindow();
                stageHandler.getBagWindow().openWindow();
                break;
            case TRADE_REQUEST_TARGET_DECLINE:
                stageHandler.getTradeWindow().closeTradeWindow();
                break;
            case TRADE_REQUEST_SERVER_TIMED_OUT:
                ActorUtil.fadeOutWindow(stageHandler.getIncomingTradeRequestWindow());
                break;

            // Stage 3: Trade started -> adding/removing items from trade window
            case TRADE_ITEM_ADD:
                stageHandler.getTradeWindow().addItemFromPacket(packetData.itemId, packetData.itemAmount);
                break;
            case TRADE_ITEM_REMOVE:
                stageHandler.getTradeWindow().removeItemFromPacket(packetData.itemSlot);
                break;

            // Stage 4: First Trade Confirm (items are in window, do trade or cancel)
            case TRADE_OFFER_CONFIRM:
                stageHandler.getTradeWindow().confirmTradeUI(packetData.confirmedPlayerUUID);
                break;
            case TRADE_OFFER_UNCONFIRM:
                stageHandler.getTradeWindow().unconfirmTradeUI(packetData.confirmedPlayerUUID);
                break;

            // Stage 5: Final trade confirm
            case TRADE_OFFER_COMPLETE:
                // Server will send items in different packet to client
                // Close and gameQuitReset trade window
                stageHandler.getTradeWindow().closeTradeWindow();
                clientMain.getTradeManager().setTradeUUID(null); // Reset trade UUID
                break;

            // Generic trade cancel
            case TRADE_CANCELED:
                stageHandler.getTradeWindow().closeTradeWindow();

                if (stageHandler.getIncomingTradeRequestWindow().isVisible()) {
                    ActorUtil.fadeOutWindow(stageHandler.getIncomingTradeRequestWindow());
                }
                break;

            // ACCEPTED TRADE STATUS
            default:
                println(getClass(), "onEvent unhandeled trade status: " + packetData.tradeStatusOpcode, true, true);
                break;
        }
    }

    @AllArgsConstructor
    static class TradeRequestPacket extends PacketData {
        private final TradeStatusOpcode tradeStatusOpcode;
        private final int tradeUUID;
        private final short tradeTargetUUID;
        private final short confirmedPlayerUUID;
        private final int itemId;
        private final int itemAmount;
        private final byte itemSlot;
    }
}
