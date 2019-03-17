package com.valenguard.client.network.packet.out;

import com.valenguard.client.game.rpg.EntityShopAction;
import com.valenguard.client.network.shared.Opcodes;

public class EntityShopPacketOut extends AbstractClientOutPacket {

    private EntityShopAction entityShopAction;

    public EntityShopPacketOut(EntityShopAction entityShopAction) {
        super(Opcodes.ENTITY_SHOPS);
        this.entityShopAction = entityShopAction;
    }

    @Override
    void createPacket(ValenguardOutputStream write) {
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