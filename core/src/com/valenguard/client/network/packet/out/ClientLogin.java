package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.PlayerSession;
import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ClientLogin extends ClientOutPacket {

    private final PlayerSession playerSession;

    public ClientLogin(PlayerSession playerSession) {
        super(Opcodes.CLIENT_LOGIN);
        this.playerSession = playerSession;
    }

    @Override
    protected void createPacket(DataOutputStream write) throws IOException {
        write.writeUTF(playerSession.getUsername());
        write.writeUTF(playerSession.getPassword());
    }
}
