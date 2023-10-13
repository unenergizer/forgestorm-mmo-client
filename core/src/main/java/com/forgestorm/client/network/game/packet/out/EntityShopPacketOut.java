package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.ShopOpcodes;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class EntityShopPacketOut extends AbstractPacketOut {

    private final ShopOpcodes shopOpcode;
    private short shopSlot;
    private short entityId;

    public EntityShopPacketOut(ClientMain clientMain, ShopOpcodes shopOpcode, short data) {
        super(clientMain, Opcodes.ENTITY_SHOPS);
        this.shopOpcode = shopOpcode;

        if (shopOpcode == ShopOpcodes.START_SHOPPING) {
            entityId = data;
        } else if (shopOpcode == ShopOpcodes.BUY) {
            shopSlot = data;
        } else if (shopOpcode == ShopOpcodes.STOP_SHOPPING) {
            throw new RuntimeException("This opcodes needs no additional parameters.");
        }
    }

    public EntityShopPacketOut(ClientMain clientMain, ShopOpcodes shopOpcode) {
        super(clientMain, Opcodes.ENTITY_SHOPS);

        if (shopOpcode != ShopOpcodes.STOP_SHOPPING) {
            throw new RuntimeException("This opcodes needs additional parameters.");
        }

        this.shopOpcode = shopOpcode;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeByte(shopOpcode.getShopOpcodeByte());

        switch (shopOpcode) {
            case BUY:
                write.writeShort(shopSlot);
                break;
            case START_SHOPPING:
                write.writeShort(entityId);
                break;
        }
    }
}
