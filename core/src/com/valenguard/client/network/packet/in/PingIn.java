package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.packet.out.PingOut;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketListener;

public class PingIn implements PacketListener {

    @Opcode(getOpcode = Opcodes.PING)
    public void onIncomingPing(ClientHandler clientHandler) {
        long serverCalcPing = clientHandler.readLong();
        Valenguard.getInstance().setPing(serverCalcPing);
        new PingOut().sendPacket();
    }
}
