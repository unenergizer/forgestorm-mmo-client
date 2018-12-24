package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.PlayerSession;
import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClientLoginPacketOut extends ClientAbstractOutPacket {

    private final PlayerSession playerSession;

    public ClientLoginPacketOut(PlayerSession playerSession) {
        super(Opcodes.CLIENT_LOGIN);
        this.playerSession = playerSession;
    }

    @Override
    protected void createPacket(DataOutputStream write) throws IOException {
        write.writeUTF(playerSession.getUsername());
        write.writeUTF(playerSession.getPassword());
    }
}
