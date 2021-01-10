package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.game.world.maps.Warp;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.WORLD_CHUNK_WARP)
public class TileWarpPacketIn implements PacketListener<TileWarpPacketIn.ChunkWarpDataPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        boolean clearWarps = clientHandler.readBoolean();

        short chunkLocation = clientHandler.readShort();

        String worldName = clientHandler.readString();
        int toX = clientHandler.readInt();
        int toY = clientHandler.readInt();
        MoveDirection facingDirection = MoveDirection.getDirection(clientHandler.readByte());
        Warp warp = new Warp(new Location(worldName, toX, toY), facingDirection);

        return new ChunkWarpDataPacket(clearWarps, chunkLocation, warp);
    }

    @Override
    public void onEvent(ChunkWarpDataPacket packetData) {
        Location warpLocation = packetData.warp.getLocation();
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.findChunk(warpLocation.getX(), warpLocation.getY());

        if (packetData.clearWarps) worldChunk.clearTileWarps();

        // Need to set the location of the warp....
        worldChunk.addTileWarp(packetData.chunkLocation, packetData.warp);
    }

    @AllArgsConstructor
    static class ChunkWarpDataPacket extends PacketData {
        private final boolean clearWarps;
        private final int chunkLocation;
        private final Warp warp;
    }
}
