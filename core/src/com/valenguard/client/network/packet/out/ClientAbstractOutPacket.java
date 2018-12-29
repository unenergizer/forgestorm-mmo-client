package com.valenguard.client.network.packet.out;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Write;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ClientAbstractOutPacket {

    /**
     * Opcode to send with the out-going packet.
     */
    private final byte opcode;

    /**
     * Used to easily send out packets to the server.
     */
    private final ClientHandler clientHandler = Valenguard.clientConnection.getClientHandler();

    ClientAbstractOutPacket(byte opcode) {
        this.opcode = opcode;
    }

    /**
     * Sends the packet to the player.
     */
    public void sendPacket() {
        Valenguard.getInstance().getOutputStreamManager().addClientOutPacket(this);
    }

    void writeData() {
        clientHandler.write(opcode, new Write() {
            @Override
            public void accept(DataOutputStream write) throws IOException {
                createPacket(write);
            }
        });
    }

    /**
     * Creates the packet.
     */
    protected abstract void createPacket(DataOutputStream write) throws IOException;
}
