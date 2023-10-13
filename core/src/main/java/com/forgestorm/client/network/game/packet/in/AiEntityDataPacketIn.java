package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.EntityAlignment;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.AI_ENTITY_UPDATE_OUT)
public class AiEntityDataPacketIn implements PacketListener<AiEntityDataPacketIn.AiEntityData> {

    private static final byte ALIGNMENT_INDEX = 0x01;
    private static final byte BANK_KEEPER_INDEX = 0x02;
    private final ClientMain clientMain;

    public AiEntityDataPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        short entityId = clientHandler.readShort();
        byte dataBits = clientHandler.readByte();
        EntityAlignment entityAlignment = null;
        boolean isBankKeeper = false;

        if ((dataBits & ALIGNMENT_INDEX) != 0) {
            entityAlignment = EntityAlignment.getEntityAlignment(clientHandler.readByte());
        } else if ((dataBits & BANK_KEEPER_INDEX) != 0) {
            isBankKeeper = clientHandler.readBoolean();
        }

        return new AiEntityData(entityId, dataBits, entityAlignment, isBankKeeper);
    }

    @Override
    public void onEvent(AiEntityData packetData) {
        AiEntity aiEntity = clientMain.getEntityManager().getAiEntity(packetData.entityId);

        if ((packetData.dataBits & ALIGNMENT_INDEX) != 0) {
            aiEntity.setAlignment(packetData.entityAlignment);
        } else if ((packetData.dataBits & BANK_KEEPER_INDEX) != 0) {
            aiEntity.setBankKeeper(packetData.isBankKeeper);
        }
    }

    @AllArgsConstructor
    static class AiEntityData extends PacketData {
        private final short entityId;
        private final byte dataBits;
        private final EntityAlignment entityAlignment;
        private final boolean isBankKeeper;
    }
}
