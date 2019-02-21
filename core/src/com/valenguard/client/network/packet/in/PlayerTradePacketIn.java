package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.TradeStatus;
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

        final TradeStatus tradeStatus = TradeStatus.getTradeStatusOpcode(clientHandler.readByte());
        int tradeUUID = 0;

        switch (tradeStatus) {
            case TRADE_REQUEST_PLAYER_SENDER:
            case TRADE_REQUEST_PLAYER_TARGET:
                tradeUUID = clientHandler.readInt();
                break;
            default:
                println(getClass(), "Decode unused trade status: " + tradeStatus, true, true);
                break;
        }

        return new TradeRequestPacket(tradeStatus, tradeUUID);
    }

    @Override
    public void onEvent(TradeRequestPacket packetData) {
        StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

        switch (packetData.tradeStatus) {
            case TRADE_REQUEST_PLAYER_SENDER:
                Valenguard.getInstance().getTradeManager().setTradeUUID(packetData.tradeUUID);
                break;
            case TRADE_REQUEST_PLAYER_TARGET:
                Valenguard.getInstance().getTradeManager().setTradeUUID(packetData.tradeUUID);
                stageHandler.getIncomingTradeRequestWindow().setVisible(true);
                break;
            case TRADE_REQUEST_TIMED_OUT:
                stageHandler.getIncomingTradeRequestWindow().setVisible(false);
                break;
            case TRADE_REQUEST_ACCEPT:
                stageHandler.getTradeWindow().setVisible(true);
                break;
            case TRADE_REQUEST_DECLINE:
                stageHandler.getTradeWindow().closeTradeWindow();
                break;
            case TRADE_OFFER_ACCEPT:
                // TODO: Delete traded items (server will send new items [InventoryPacketIn])
                stageHandler.getTradeWindow().closeTradeWindow();
                break;
            case TRADE_OFFER_DECLINE:
                stageHandler.getTradeWindow().closeTradeWindow();
                break;
            default:
                println(getClass(), "onEvent unused trade status: " + packetData.tradeStatus, true, true);
                break;
        }
    }

    @AllArgsConstructor
    class TradeRequestPacket extends PacketData {
        private final TradeStatus tradeStatus;
        private final int tradeUUID;
    }
}
