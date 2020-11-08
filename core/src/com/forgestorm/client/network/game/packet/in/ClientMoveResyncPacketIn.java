package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.CLIENT_MOVE_RESYNC)
public class ClientMoveResyncPacketIn implements PacketListener<ClientMoveResyncPacketIn.ClientMoveResyncPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short futureX = clientHandler.readShort();
        final short futureY = clientHandler.readShort();
        return new ClientMoveResyncPacket(futureX, futureY);
    }

    @Override
    public void onEvent(ClientMoveResyncPacket packetData) {

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Location resyncLocation = new Location(playerClient.getWorldName(), packetData.futureX, packetData.futureY);

        playerClient.setCurrentMapLocation(new Location(resyncLocation));
        playerClient.setFutureMapLocation(new Location(resyncLocation));
        playerClient.setDrawX(resyncLocation.getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(resyncLocation.getY() * ClientConstants.TILE_SIZE);

        ClientMain.getInstance().getClientMovementProcessor().resetInput();

    }

    @AllArgsConstructor
    class ClientMoveResyncPacket extends PacketData {
        private final short futureX;
        private final short futureY;
    }
}
