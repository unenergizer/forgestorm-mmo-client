package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.ClientMain;
import com.valenguard.client.network.game.packet.out.PingPacketOut;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.PING)
public class PingPacketIn implements PacketListener<PingPacketIn.PingInPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new PingInPacket(System.currentTimeMillis(), clientHandler.readLong());
    }

    @Override
    public void onEvent(PingInPacket packetData) {
        //TODO: Come back and see if we need to send the server the clients process time in order to get the accurate ping on the server end.
        // Also, this could be a bad idea as the client could not be in sync or the client hacker sends back bad times
        ClientMain.connectionManager.getClientGameConnection().setPing(packetData.serverCalcPing - (System.currentTimeMillis() - packetData.firstTime));
        new PingPacketOut().sendPacket();
    }

    @AllArgsConstructor
    class PingInPacket extends PacketData {
        private final long firstTime;
        private final long serverCalcPing;
    }
}
