package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_DESPAWN)
public class EntityDespawnPacketIn implements PacketListener<EntityDespawnPacketIn.EntityDespawnPacket> {

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public EntityDespawnPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityUUID = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();

        println(getClass(), "###[ DESPAWN IN ]################################", false, PRINT_DEBUG);
        println(getClass(), "EntityID: " + entityUUID, false, PRINT_DEBUG);
        println(getClass(), "EntityType: " + EntityType.getEntityType(entityType), false, PRINT_DEBUG);

        return new EntityDespawnPacket(entityUUID, EntityType.getEntityType(entityType));
    }

    @Override
    public void onEvent(EntityDespawnPacket packetData) {
        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                println(getClass(), "Tried to despawn CLIENT_PLAYER type!", true);
                break;
            case PLAYER:
                clientMain.getEntityManager().removePlayerEntity(packetData.entityId);
                break;
            case MONSTER:
            case NPC:
                clientMain.getEntityManager().removeAiEntity(packetData.entityId);
                break;
            case ITEM_STACK:
                clientMain.getEntityManager().removeItemStackDrop(packetData.entityId);
                break;
            case SKILL_NODE:
                clientMain.getEntityManager().removeStationaryEntity(packetData.entityId);
                break;
        }
        clientMain.getStageHandler().getEntityDropDownMenu().closeDropDownMenu(packetData.entityType, packetData.entityId);
        clientMain.getStageHandler().getTargetStatusBar().hideTargetStatusBar(packetData.entityId);
    }

    @AllArgsConstructor
    static class EntityDespawnPacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
    }
}
