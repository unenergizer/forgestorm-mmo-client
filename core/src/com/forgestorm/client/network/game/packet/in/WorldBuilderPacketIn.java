package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.WORLD_BUILDER)
public class WorldBuilderPacketIn implements PacketListener<WorldBuilderPacketIn.WorldBuilderPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        LayerDefinition layerDefinition = LayerDefinition.getLayerDefinition(clientHandler.readByte());
        int textureId = clientHandler.readInt();
        int tileX = clientHandler.readInt();
        int tileY = clientHandler.readInt();
        return new WorldBuilderPacket(layerDefinition, textureId, tileX, tileY);
    }

    @Override
    public void onEvent(WorldBuilderPacket packetData) {
        ClientMain.getInstance().getWorldBuilder().placeTile(
                packetData.layerDefinition,
                packetData.textureId,
                packetData.tileX,
                packetData.tileY,
                false);
    }

    @AllArgsConstructor
    class WorldBuilderPacket extends PacketData {
        private final LayerDefinition layerDefinition;
        private final int textureId;
        private final int tileX;
        private final int tileY;
    }
}
