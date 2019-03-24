package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.game.inventory.TradePacketInfoOut;
import com.valenguard.client.network.game.shared.Opcodes;

import static com.valenguard.client.util.Log.println;

public class PlayerTradePacketOut extends AbstractClientOutPacket {

    private TradePacketInfoOut tradePacketInfoOut;

    public PlayerTradePacketOut(TradePacketInfoOut tradePacketInfoOut) {
        super(Opcodes.PLAYER_TRADE);
        this.tradePacketInfoOut = tradePacketInfoOut;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        println(getClass(), "Opcode: " + tradePacketInfoOut.getTradeStatusOpcode());

        write.writeByte(tradePacketInfoOut.getTradeStatusOpcode().getTradeOpcodeByte());

        switch (tradePacketInfoOut.getTradeStatusOpcode()) {
            case TRADE_REQUEST_INIT_TARGET:
                write.writeShort(tradePacketInfoOut.getTargetEntityUUID());
                break;
            case TRADE_REQUEST_TARGET_ACCEPT:
            case TRADE_REQUEST_TARGET_DECLINE:
            case TRADE_OFFER_CONFIRM:
            case TRADE_OFFER_UNCONFIRM:
            case TRADE_CANCELED:
                write.writeInt(tradePacketInfoOut.getTradeUUID());
                break;
            case TRADE_ITEM_ADD:
            case TRADE_ITEM_REMOVE:
                write.writeInt(tradePacketInfoOut.getTradeUUID());
                write.writeByte(tradePacketInfoOut.getItemSlot());
                break;
            default:
                println(getClass(), "Create unused trade status", true, true);
                break;
        }

        println(getClass(), "trade packet out sent");
    }
}
