package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.packet.out.PingOut;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.PING)
public class PingIn implements PacketListener<PingIn.PingInPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new PingInPacket(System.currentTimeMillis(), clientHandler.readLong());
    }

    @Override
    public void onEvent(PingInPacket packetData) {
        //TODO: Come back and see if we need to send the server the clients process time in order to get the accurate ping on the server end.
        // Also, this could be a bad idea as the client could not be in sync or the client hacker sends back bad times
        Valenguard.clientConnection.setPing(packetData.serverCalcPing - (System.currentTimeMillis() - packetData.firstTime));
        new PingOut().sendPacket();
    }

    @AllArgsConstructor
    class PingInPacket extends PacketData {
        private final long firstTime;
        private final long serverCalcPing;
    }
}
