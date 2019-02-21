package com.valenguard.client.network.packet.out;

import com.valenguard.client.game.inventory.TradePacketInfoOut;
import com.valenguard.client.network.shared.Opcodes;

import static com.valenguard.client.util.Log.println;

public class PlayerTradePacketOut extends ClientAbstractOutPacket {

    private TradePacketInfoOut tradePacketInfoOut;

    public PlayerTradePacketOut(TradePacketInfoOut tradePacketInfoOut) {
        super(Opcodes.PLAYER_TRADE);
        this.tradePacketInfoOut = tradePacketInfoOut;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeByte(tradePacketInfoOut.getTradeStatus().getTradeOpcodeByte());

        switch (tradePacketInfoOut.getTradeStatus()) {
            case TRADE_REQUEST_PLAYER_TARGET:
                write.writeShort(tradePacketInfoOut.getTargetEntityUUID());
                break;
            case TRADE_REQUEST_ACCEPT:
                write.writeInt(tradePacketInfoOut.getTradeUUID());
                break;
            case TRADE_REQUEST_DECLINE:
            case TRADE_OFFER_DECLINE:
                write.writeInt(tradePacketInfoOut.getTradeUUID());
                break;
            case TRADE_OFFER_ACCEPT:
                write.writeInt(tradePacketInfoOut.getTradeUUID());
                break;
            default:
                println(getClass(), "Create unused trade status", true, true);
                break;
        }
    }
}
