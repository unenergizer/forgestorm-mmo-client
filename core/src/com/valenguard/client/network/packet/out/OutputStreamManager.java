package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.ClientHandler;

import java.util.LinkedList;
import java.util.Queue;

public class OutputStreamManager {

    private static final int MAX_BUFFER_SIZE = 500;

    private final Queue<ClientAbstractOutPacket> outputContexts = new LinkedList<ClientAbstractOutPacket>();

    public void sendPackets(ClientHandler clientHandler) {
        int bufferOffsetCheck = 0;
        ClientAbstractOutPacket clientAbstractOutPacket;
        while ((clientAbstractOutPacket = outputContexts.poll()) != null) {

            int thisBufferSize = clientHandler.fillCurrentBuffer(clientAbstractOutPacket);
            bufferOffsetCheck += thisBufferSize;

            if (bufferOffsetCheck > MAX_BUFFER_SIZE) { // exceeds buffer limit so we should flush what we have written so far

                // Writing any left over data that was not already written.
                clientHandler.writeBuffers();
                clientHandler.flushBuffer();

                bufferOffsetCheck = thisBufferSize;

                clientHandler.getValenguardOutputStream().createNewBuffers(clientAbstractOutPacket);
                // This happened to be the last packet so we should add the
                // to be written. Write and flush it.
                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }

            } else { // The current buffer fits into the current packet

                ValenguardOutputStream valenguardOutputStream = clientHandler.getValenguardOutputStream();

                if (!valenguardOutputStream.currentBuffersInitialized()) {
                    valenguardOutputStream.createNewBuffers(clientAbstractOutPacket);
                } else {

                    boolean opcodesMatch = valenguardOutputStream.doOpcodesMatch(clientAbstractOutPacket);
                    if (opcodesMatch) {
                        valenguardOutputStream.appendBewBuffer();
                    } else {
                        clientHandler.writeBuffers();
                        valenguardOutputStream.createNewBuffers(clientAbstractOutPacket);
                    }
                }

                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }
            }

        }
    }

    void addClientOutPacket(ClientAbstractOutPacket clientAbstractOutPacket) {
        outputContexts.add(clientAbstractOutPacket);
    }
}
