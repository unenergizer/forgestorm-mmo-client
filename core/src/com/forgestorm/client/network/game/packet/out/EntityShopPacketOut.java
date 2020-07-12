package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.rpg.EntityShopAction;
import com.forgestorm.client.network.game.shared.Opcodes;

public class EntityShopPacketOut extends AbstractClientPacketOut {

    private EntityShopAction entityShopAction;

    public EntityShopPacketOut(EntityShopAction entityShopAction) {
        super(Opcodes.ENTITY_SHOPS);
        this.entityShopAction = entityShopAction;
    }

    @Override
    void createPacket(ForgeStormOutputStream write) {
        write.writeByte(entityShopAction.getShopOpcode().getShopOpcodeByte());

        switch (entityShopAction.getShopOpcode()) {
            case BUY:
                write.writeShort(entityShopAction.getShopSlot());
                break;
            case START_SHOPPING:
                write.writeShort(entityShopAction.getEntityId());
                break;
        }
    }
}
