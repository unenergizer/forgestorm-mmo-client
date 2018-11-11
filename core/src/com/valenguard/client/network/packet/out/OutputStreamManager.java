package com.valenguard.client.network.packet.out;

import java.util.LinkedList;
import java.util.Queue;

public class OutputStreamManager {

    private final Queue<ClientOutPacket> outputContexts = new LinkedList<ClientOutPacket>();

    public void sendPackets() {
        ClientOutPacket clientOutPacket;
        while ((clientOutPacket = outputContexts.poll()) != null) {
            clientOutPacket.writeData();
        }
    }

    void addClientOutPacket(ClientOutPacket clientOutPacket) {
        outputContexts.add(clientOutPacket);
    }
}
