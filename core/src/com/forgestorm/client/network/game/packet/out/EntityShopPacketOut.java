package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.rpg.EntityShopAction;
import com.forgestorm.client.game.rpg.ShopOpcodes;
import com.forgestorm.client.network.game.shared.Opcodes;

public class EntityShopPacketOut extends AbstractClientPacketOut {

    private ShopOpcodes shopOpcode;
    private short shopSlot;
    private short entityId;

    public EntityShopPacketOut(EntityShopAction entityShopAction) {
        super(Opcodes.ENTITY_SHOPS);
        shopOpcode = entityShopAction.getShopOpcode();
        shopSlot = entityShopAction.getShopSlot();
        entityId = entityShopAction.getEntityId();
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
