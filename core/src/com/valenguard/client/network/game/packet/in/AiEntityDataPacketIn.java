package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.entities.AiEntity;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.rpg.EntityAlignment;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.AIENTITY_UPDATE_OUT)
public class AiEntityDataPacketIn implements PacketListener<AiEntityDataPacketIn.AiEntityData> {

    private static final byte ALIGNMENT_INDEX = 0x01;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        short entityId = clientHandler.readShort();
        byte dataBits = clientHandler.readByte();
        EntityAlignment entityAlignment = null;

        if ((dataBits & ALIGNMENT_INDEX) != 0) {
            entityAlignment = EntityAlignment.getEntityAlignment(clientHandler.readByte());
        }

        return new AiEntityData(entityId, dataBits, entityAlignment);
    }

    @Override
    public void onEvent(AiEntityData packetData) {
        AiEntity aiEntity = EntityManager.getInstance().getAiEntity(packetData.entityId);

        if ((packetData.dataBits & ALIGNMENT_INDEX) != 0) {
            aiEntity.setAlignment(packetData.entityAlignment);
        }
    }

    @AllArgsConstructor
    class AiEntityData extends PacketData {
        private short entityId;
        private byte dataBits;
        private EntityAlignment entityAlignment;
    }
}
