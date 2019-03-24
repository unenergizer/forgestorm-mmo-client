package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.network.game.shared.ClientHandler;

import java.util.LinkedList;
import java.util.Queue;

public class OutputStreamManager {

    private static final int MAX_BUFFER_SIZE = 500;

    private final Queue<AbstractClientOutPacket> outputContexts = new LinkedList<AbstractClientOutPacket>();

    public void sendPackets(ClientHandler clientHandler) {
        int bufferOffsetCheck = 0;
        AbstractClientOutPacket abstractClientOutPacket;
        while ((abstractClientOutPacket = outputContexts.poll()) != null) {

            int thisBufferSize = clientHandler.fillCurrentBuffer(abstractClientOutPacket);
            bufferOffsetCheck += thisBufferSize;

            if (bufferOffsetCheck > MAX_BUFFER_SIZE) { // exceeds buffer limit so we should flush what we have written so far

                // Writing any left over data that was not already written.
                clientHandler.writeBuffers();
                clientHandler.flushBuffer();

                bufferOffsetCheck = thisBufferSize;

                clientHandler.getValenguardOutputStream().createNewBuffers(abstractClientOutPacket);
                // This happened to be the last packet so we should add the
                // to be written. Write and flush it.
                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }

            } else { // The current buffer fits into the current packet

                ValenguardOutputStream valenguardOutputStream = clientHandler.getValenguardOutputStream();

                if (!valenguardOutputStream.currentBuffersInitialized()) {
                    valenguardOutputStream.createNewBuffers(abstractClientOutPacket);
                } else {

                    boolean opcodesMatch = valenguardOutputStream.doOpcodesMatch(abstractClientOutPacket);
                    if (opcodesMatch) {
                        valenguardOutputStream.appendBewBuffer();
                    } else {
                        clientHandler.writeBuffers();
                        valenguardOutputStream.createNewBuffers(abstractClientOutPacket);
                    }
                }

                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }
            }

        }
    }

    void addClientOutPacket(AbstractClientOutPacket abstractClientOutPacket) {
        outputContexts.add(abstractClientOutPacket);
    }
}
