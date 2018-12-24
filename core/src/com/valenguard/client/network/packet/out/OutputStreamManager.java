package com.valenguard.client.network.packet.out;

import java.util.LinkedList;
import java.util.Queue;

public class OutputStreamManager {

    private final Queue<ClientAbstractOutPacket> outputContexts = new LinkedList<ClientAbstractOutPacket>();

    public void sendPackets() {
        ClientAbstractOutPacket clientAbstractOutPacket;
        while ((clientAbstractOutPacket = outputContexts.poll()) != null) {
            clientAbstractOutPacket.writeData();
        }
    }

    void addClientOutPacket(ClientAbstractOutPacket clientAbstractOutPacket) {
        outputContexts.add(clientAbstractOutPacket);
    }
}
