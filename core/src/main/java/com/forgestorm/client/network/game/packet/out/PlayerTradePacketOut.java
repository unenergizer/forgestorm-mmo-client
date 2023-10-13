package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.item.trade.TradePacketInfoOut;
import com.forgestorm.shared.game.world.item.trade.TradeStatusOpcode;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class PlayerTradePacketOut extends AbstractPacketOut {

    private final TradeStatusOpcode tradeStatusOpcode;
    private final int tradeUUID;
    private final short targetEntityUUID;
    private final byte itemSlot;

    public PlayerTradePacketOut(ClientMain clientMain, TradePacketInfoOut tradePacketInfoOut) {
        super(clientMain, Opcodes.PLAYER_TRADE);
        tradeStatusOpcode = tradePacketInfoOut.getTradeStatusOpcode();
        tradeUUID = tradePacketInfoOut.getTradeUUID();
        targetEntityUUID = tradePacketInfoOut.getTargetEntityUUID();
        itemSlot = tradePacketInfoOut.getItemSlot();
    }

    @Override
    public void createPacket(GameOutputStream write) {
        println(getClass(), "Opcode: " + tradeStatusOpcode);

        write.writeByte(tradeStatusOpcode.getTradeOpcodeByte());

        switch (tradeStatusOpcode) {
            case TRADE_REQUEST_INIT_TARGET:
                write.writeShort(targetEntityUUID);
                break;
            case TRADE_REQUEST_TARGET_ACCEPT:
            case TRADE_REQUEST_TARGET_DECLINE:
            case TRADE_OFFER_CONFIRM:
            case TRADE_OFFER_UNCONFIRM:
            case TRADE_CANCELED:
                write.writeInt(tradeUUID);
                break;
            case TRADE_ITEM_ADD:
            case TRADE_ITEM_REMOVE:
                write.writeInt(tradeUUID);
                write.writeByte(itemSlot);
                break;
            default:
                println(getClass(), "Create unused trade status", true, true);
                break;
        }

        println(getClass(), "trade packet out sent");
    }
}
