package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.network.game.LoginCredentials;
import com.valenguard.client.network.game.shared.Opcodes;

class LoginPacketOut extends AbstractClientOutPacket {

    private final LoginCredentials loginCredentials;

    public LoginPacketOut(LoginCredentials loginCredentials) {
        super(Opcodes.CLIENT_LOGIN);
        this.loginCredentials = loginCredentials;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeString(loginCredentials.getUsername());
        write.writeString(loginCredentials.getPassword());
    }
}
