package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.PlayerSession;
import com.valenguard.client.network.shared.Opcodes;

public class ClientLoginPacketOut extends ClientAbstractOutPacket {

    private final PlayerSession playerSession;

    public ClientLoginPacketOut(PlayerSession playerSession) {
        super(Opcodes.CLIENT_LOGIN);
        this.playerSession = playerSession;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeString(playerSession.getUsername());
        write.writeString(playerSession.getPassword());
    }
}
