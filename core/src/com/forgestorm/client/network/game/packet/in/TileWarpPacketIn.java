package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.WORLD_CHUNK_WARP)
public class TileWarpPacketIn implements PacketListener<TileWarpPacketIn.ChunkWarpDataPacket> {

    private ClientHandler clientHandler;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        int x = clientHandler.readInt();
        int y = clientHandler.readInt();
        return new ChunkWarpDataPacket(x, y);
    }

    @Override
    public void onEvent(ChunkWarpDataPacket packetData) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.findChunk(packetData.x, packetData.y);
        // Need to set the location of the warp....
//        worldChunk.addTileWarp();
    }

    @AllArgsConstructor
    class ChunkWarpDataPacket extends PacketData {
        private int x, y;
    }
}
