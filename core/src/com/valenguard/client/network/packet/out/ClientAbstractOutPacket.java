package com.valenguard.client.network.packet.out;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.shared.ClientHandler;

import lombok.Getter;

public abstract class ClientAbstractOutPacket {

    /**
     * Opcode to send with the out-going packet.
     */
    @Getter
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

    /**
     * Creates the packet.
     */
    abstract void createPacket(ValenguardOutputStream write);
}
