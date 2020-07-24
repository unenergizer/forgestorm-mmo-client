package com.forgestorm.client.network.game.packet.out;

import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.network.game.shared.ClientHandler;

import java.util.LinkedList;
import java.util.Queue;

import static com.forgestorm.client.util.Log.println;

public class OutputStreamManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;
    private static final int MAX_BUFFER_SIZE = 500;

    private final Queue<AbstractClientPacketOut> outputContexts = new LinkedList<AbstractClientPacketOut>();

    public void sendPackets(ClientHandler clientHandler) {
        int bufferOffsetCheck = 0;
        AbstractClientPacketOut abstractClientPacketOut;
        while ((abstractClientPacketOut = outputContexts.poll()) != null) {

            println(getClass(), "PACKET OUT: " + abstractClientPacketOut, false, PRINT_DEBUG);

            int thisBufferSize = clientHandler.fillCurrentBuffer(abstractClientPacketOut);
            bufferOffsetCheck += thisBufferSize;

            if (bufferOffsetCheck > MAX_BUFFER_SIZE) { // exceeds buffer limit so we should flush what we have written so far

                // Writing any left over data that was not already written.
                clientHandler.writeBuffers();
                clientHandler.flushBuffer();

                bufferOffsetCheck = thisBufferSize;

                clientHandler.getForgeStormOutputStream().createNewBuffers(abstractClientPacketOut);
                // This happened to be the last packet so we should add the
                // to be written. Write and flush it.
                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }

            } else { // The current buffer fits into the current packet

                ForgeStormOutputStream forgeStormOutputStream = clientHandler.getForgeStormOutputStream();

                if (!forgeStormOutputStream.currentBuffersInitialized()) {
                    forgeStormOutputStream.createNewBuffers(abstractClientPacketOut);
                } else {

                    boolean opcodesMatch = forgeStormOutputStream.doOpcodesMatch(abstractClientPacketOut);
                    if (opcodesMatch) {
                        forgeStormOutputStream.appendBewBuffer();
                    } else {
                        clientHandler.writeBuffers();
                        forgeStormOutputStream.createNewBuffers(abstractClientPacketOut);
                    }
                }

                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }
            }

        }
    }

    void addClientOutPacket(AbstractClientPacketOut abstractClientPacketOut) {
        outputContexts.add(abstractClientPacketOut);
    }

    @Override
    public void dispose() {
        outputContexts.clear();
    }
}
