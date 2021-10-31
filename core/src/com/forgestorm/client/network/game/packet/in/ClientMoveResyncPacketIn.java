package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.CLIENT_MOVE_RESYNC)
public class ClientMoveResyncPacketIn implements PacketListener<ClientMoveResyncPacketIn.ClientMoveResyncPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final int futureX = clientHandler.readInt();
        final int futureY = clientHandler.readInt();
        final short futureZ = clientHandler.readShort();
        return new ClientMoveResyncPacket(futureX, futureY, futureZ);
    }

    @Override
    public void onEvent(ClientMoveResyncPacket packetData) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Location resyncLocation = new Location(playerClient.getWorldName(), packetData.futureX, packetData.futureY, packetData.futureZ);

        playerClient.setCurrentMapLocation(new Location(resyncLocation));
        playerClient.setFutureMapLocation(new Location(resyncLocation));
        playerClient.setDrawX(resyncLocation.getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(resyncLocation.getY() * ClientConstants.TILE_SIZE);

        ClientMain.getInstance().getClientMovementProcessor().resetInput();
    }

    @AllArgsConstructor
    class ClientMoveResyncPacket extends PacketData {
        private final int futureX;
        private final int futureY;
        private final short futureZ;
    }
}
