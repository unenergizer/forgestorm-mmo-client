package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.DoorManager;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.DOOR_INTERACT)
public class DoorInteractPacketIn implements PacketListener<DoorInteractPacketIn.DoorStatusPacket> {

    private final static boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        byte doorStatus = clientHandler.readByte();
        int tileX = clientHandler.readInt();
        int tileY = clientHandler.readInt();
        short worldZ = clientHandler.readShort();

        return new DoorStatusPacket(DoorManager.DoorStatus.getDoorStatus(doorStatus), tileX, tileY, worldZ);
    }

    @Override
    public void onEvent(DoorStatusPacket packetData) {
        println(getClass(), "DoorStatus: " + packetData.doorStatus, false, PRINT_DEBUG);
        println(getClass(), "TileX: " + packetData.tileX, false, PRINT_DEBUG);
        println(getClass(), "TileY: " + packetData.tileY, false, PRINT_DEBUG);
        println(getClass(), "TileZ: " + packetData.worldZ, false, PRINT_DEBUG);

        ClientMain.getInstance().getDoorManager().networkToggleDoor(packetData.doorStatus, packetData.tileX, packetData.tileY, packetData.worldZ, true);
    }

    @AllArgsConstructor
    static class DoorStatusPacket extends PacketData {
        private final DoorManager.DoorStatus doorStatus;
        private final int tileX;
        private final int tileY;
        private final short worldZ;
    }
}
