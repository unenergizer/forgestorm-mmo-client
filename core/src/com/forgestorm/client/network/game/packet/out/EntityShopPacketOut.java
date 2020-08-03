package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.rpg.ShopOpcodes;
import com.forgestorm.client.network.game.shared.Opcodes;

public class EntityShopPacketOut extends AbstractClientPacketOut {

    private ShopOpcodes shopOpcode;
    private short shopSlot;
    private short entityId;

    public EntityShopPacketOut(ShopOpcodes shopOpcode, short data) {
        super(Opcodes.ENTITY_SHOPS);
        this.shopOpcode = shopOpcode;

        if (shopOpcode == ShopOpcodes.START_SHOPPING) {
            entityId = data;
        } else if (shopOpcode == ShopOpcodes.BUY) {
            shopSlot = data;
        } else if (shopOpcode == ShopOpcodes.STOP_SHOPPING) {
            throw new RuntimeException("This opcodes needs no additional parameters.");
        }
    }

    public EntityShopPacketOut(ShopOpcodes shopOpcode) {
        super(Opcodes.ENTITY_SHOPS);

        if (shopOpcode != ShopOpcodes.STOP_SHOPPING) {
            throw new RuntimeException("This opcodes needs additional parameters.");
        }

        this.shopOpcode = shopOpcode;
    }

    @Override
    void createPacket(ForgeStormOutputStream write) {
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
