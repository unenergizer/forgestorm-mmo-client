package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.PlayerSession;
import com.valenguard.client.network.shared.Opcodes;

class LoginPacketOut extends AbstractClientOutPacket {

    private final PlayerSession playerSession;

    public LoginPacketOut(PlayerSession playerSession) {
        super(Opcodes.CLIENT_LOGIN);
        this.playerSession = playerSession;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeString(playerSession.getUsername());
        write.writeString(playerSession.getPassword());
    }
}
