package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.ClientConnection;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Write;

import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class ClientOutPacket {

    /**
     * Opcode to send with the out-going packet.
     */
    protected byte opcode;

    /**
     * Used to easily send out packets to the server.
     */
    protected ClientHandler clientHandler = ClientConnection.getInstance().getClientHandler();

    public ClientOutPacket(byte opcode) {
        this.opcode = opcode;
    }

    /**
     * Sends the packet to the player.
     */
    public void sendPacket() {
        clientHandler.write(opcode, new Write() {
            @Override
            public void accept(ObjectOutputStream write) throws IOException {
                createPacket(write);
            }
        });
    }

    /**
     * Creates the packet.
     */
    protected abstract void createPacket(ObjectOutputStream write) throws IOException;
}
