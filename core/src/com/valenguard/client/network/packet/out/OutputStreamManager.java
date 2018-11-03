package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.ClientConnection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutputStreamManager implements Runnable {

    private final Queue<ClientOutPacket> outputContexts = new ConcurrentLinkedQueue<ClientOutPacket>();

    @Override
    public void run() {
        while (ClientConnection.getInstance().isConnected()) {
            ClientOutPacket clientOutPacket;
            while ((clientOutPacket = outputContexts.poll()) != null) {
                clientOutPacket.writeData();
            }
        }
    }

    void addClientOutPacket(ClientOutPacket clientOutPacket) {
        outputContexts.add(clientOutPacket);
    }
}
