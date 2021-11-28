package com.forgestorm.client.network.game.packet.out;

import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.shared.network.game.GameOutputStream;

import java.util.LinkedList;
import java.util.Queue;

import static com.forgestorm.client.util.Log.println;

public class OutputStreamManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;
    private static final int MAX_BUFFER_SIZE = 500;

    private final Queue<AbstractPacketOut> outputContexts = new LinkedList<AbstractPacketOut>();

    public void sendPackets(ClientHandler clientHandler) {
        int bufferOffsetCheck = 0;
        AbstractPacketOut abstractPacketOut;
        while ((abstractPacketOut = outputContexts.poll()) != null) {

            println(getClass(), "PACKET OUT: " + abstractPacketOut, false, PRINT_DEBUG);

            int thisBufferSize = clientHandler.fillCurrentBuffer(abstractPacketOut);
            bufferOffsetCheck += thisBufferSize;

            if (bufferOffsetCheck > MAX_BUFFER_SIZE) { // exceeds buffer limit so we should flush what we have written so far

                // Writing any left over data that was not already written.
                clientHandler.writeBuffers();
                clientHandler.flushBuffer();

                bufferOffsetCheck = thisBufferSize;

                clientHandler.getGameOutputStream().createNewBuffers(abstractPacketOut);
                // This happened to be the last packet so we should add the
                // to be written. Write and flush it.
                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }

            } else { // The current buffer fits into the current packet

                GameOutputStream gameOutputStream = clientHandler.getGameOutputStream();

                if (!gameOutputStream.currentBuffersInitialized()) {
                    gameOutputStream.createNewBuffers(abstractPacketOut);
                } else {

                    boolean opcodesMatch = gameOutputStream.doOpcodesMatch(abstractPacketOut);
                    if (opcodesMatch) {
                        gameOutputStream.appendBewBuffer();
                    } else {
                        clientHandler.writeBuffers();
                        gameOutputStream.createNewBuffers(abstractPacketOut);
                    }
                }

                if (outputContexts.peek() == null) {
                    clientHandler.writeBuffers();
                    clientHandler.flushBuffer();
                }
            }

        }
    }

    void addClientOutPacket(AbstractPacketOut abstractPacketOut) {
        outputContexts.add(abstractPacketOut);
    }

    @Override
    public void dispose() {
        outputContexts.clear();
    }
}
