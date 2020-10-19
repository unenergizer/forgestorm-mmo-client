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
        short tileX = clientHandler.readShort();
        short tileY = clientHandler.readShort();
        return new WorldBuilderPacket(layerDefinition, textureId, tileX, tileY);
    }

    @Override
    public void onEvent(WorldBuilderPacket packetData) {
        ClientMain.getInstance().getWorldBuilder().placeTile(
                packetData.layerDefinition,
                packetData.textureId,
                packetData.tileX,
                packetData.tileY);
    }

    @AllArgsConstructor
    class WorldBuilderPacket extends PacketData {
        private LayerDefinition layerDefinition;
        private int textureId;
        private short tileX;
        private short tileY;
    }
}
