package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.TradeStatusOpcode;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.PLAYER_TRADE)
public class PlayerTradePacketIn implements PacketListener<PlayerTradePacketIn.TradeRequestPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        println(getClass(), "Trade request in");

        final TradeStatusOpcode tradeStatusOpcode = TradeStatusOpcode.getTradeStatusOpcode(clientHandler.readByte());
        int tradeUUID = -1;
        short playerUUID = -2;
        int itemId = -3;
        byte itemSlot = -4;

        //noinspection ConstantConditions
        switch (tradeStatusOpcode) {
            case TRADE_REQUEST_INIT_SENDER:
            case TRADE_REQUEST_INIT_TARGET:
            case TRADE_REQUEST_TARGET_ACCEPT:
            case TRADE_REQUEST_TARGET_DECLINE:
            case TRADE_REQUEST_SERVER_TIMED_OUT:
            case TRADE_OFFER_COMPLETE:
            case TRADE_CANCELED:
                tradeUUID = clientHandler.readInt();
                break;
            case TRADE_OFFER_CONFIRM:
                tradeUUID = clientHandler.readInt();
                playerUUID = clientHandler.readShort();
                break;
            case TRADE_ITEM_ADD:
                tradeUUID = clientHandler.readInt();
                itemId = clientHandler.readInt();
                break;
            case TRADE_ITEM_REMOVE:
                tradeUUID = clientHandler.readInt();
                itemSlot = clientHandler.readByte();
                break;
            default:
                println(getClass(), "Decode unused trade status: " + tradeStatusOpcode, true, true);
                break;
        }

        return new TradeRequestPacket(tradeStatusOpcode, tradeUUID, playerUUID, itemId, itemSlot);
    }

    @Override
    public void onEvent(TradeRequestPacket packetData) {
        StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

        switch (packetData.tradeStatusOpcode) {

            // Stage 1: Init trade
            case TRADE_REQUEST_INIT_SENDER:
                Valenguard.getInstance().getTradeManager().setTradeUUID(packetData.tradeUUID);
                break;
            case TRADE_REQUEST_INIT_TARGET:
                Valenguard.getInstance().getTradeManager().setTradeUUID(packetData.tradeUUID);
                stageHandler.getIncomingTradeRequestWindow().setVisible(true);
                break;

            // Stage 2: Wait for TargetPlayer response or time out
            case TRADE_REQUEST_TARGET_ACCEPT:
                stageHandler.getTradeWindow().setVisible(true);
                break;
            case TRADE_REQUEST_TARGET_DECLINE:
                stageHandler.getTradeWindow().closeTradeWindow();
                break;
            case TRADE_REQUEST_SERVER_TIMED_OUT:
                stageHandler.getIncomingTradeRequestWindow().setVisible(false);
                break;

            // Stage 3: Trade started -> adding/removing items from trade window
            case TRADE_ITEM_ADD:
                stageHandler.getTradeWindow().addItemFromPacket(packetData.itemId);
                break;
            case TRADE_ITEM_REMOVE:
                stageHandler.getTradeWindow().removeItemFromPacket(packetData.itemSlot);
                break;

            // Stage 4: First Trade Confirm (items are in window, do trade or cancel)
            case TRADE_OFFER_CONFIRM:
                // TODO: Get entity who confirmed. Show check mark on confirmed window pane?
                stageHandler.getTradeWindow().setupConfirmButtons(packetData.playerUUID);
                break;

            // Stage 5: Final trade confirm
            case TRADE_OFFER_COMPLETE:
                // TODO: Server will send items in different packet to client
                // TODO: Close and reset trade window
                stageHandler.getTradeWindow().closeTradeWindow();
                Valenguard.getInstance().getTradeManager().setTradeUUID(null); // Reset trade UUID
                break;

            // Generic trade cancel
            case TRADE_CANCELED:
                if (stageHandler.getTradeWindow().isVisible()) {
                    stageHandler.getTradeWindow().closeTradeWindow();
                }
                if (stageHandler.getIncomingTradeRequestWindow().isVisible()) {
                    stageHandler.getIncomingTradeRequestWindow().setVisible(false);
                }
                break;

            // ACCEPTED TRADE STATUS
            default:
                println(getClass(), "onEvent unhandeled trade status: " + packetData.tradeStatusOpcode, true, true);
                break;
        }
    }

    @AllArgsConstructor
    class TradeRequestPacket extends PacketData {
        private final TradeStatusOpcode tradeStatusOpcode;
        private final int tradeUUID;
        private final short playerUUID;
        private final int itemId;
        private final byte itemSlot;
    }
}
