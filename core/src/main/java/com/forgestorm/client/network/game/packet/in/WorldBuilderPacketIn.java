package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.WORLD_BUILDER)
public class WorldBuilderPacketIn implements PacketListener<WorldBuilderPacketIn.WorldBuilderPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        LayerDefinition layerDefinition = LayerDefinition.getLayerDefinition(clientHandler.readByte());
        int textureId = clientHandler.readInt();
        int tileX = clientHandler.readInt();
        int tileY = clientHandler.readInt();
        short worldZ = clientHandler.readShort();
        return new WorldBuilderPacket(layerDefinition, textureId, tileX, tileY, worldZ);
    }

    @Override
    public void onEvent(WorldBuilderPacket packetData) {
        ClientMain.getInstance().getWorldBuilder().placeTile(
                packetData.layerDefinition,
                packetData.textureId,
                packetData.tileX,
                packetData.tileY,
                packetData.worldZ,
                false);
    }

    @AllArgsConstructor
    class WorldBuilderPacket extends PacketData {
        private final LayerDefinition layerDefinition;
        private final int textureId;
        private final int tileX;
        private final int tileY;
        private final short worldZ;
    }
}
