package com.forgestorm.shared.network.game;

import com.forgestorm.client.network.game.packet.out.AbstractPacketOut;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class GameOutputStream {

    private final DataOutputStream dataOutputStream;

    public GameOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    private int bytePosition = 0;

    private final OpcodePacketData opcodePacketData = new OpcodePacketData();

    // The current buffer being worked on.
    private final byte[] buffer = new byte[200];

    public void writeByte(byte b) {
        buffer[bytePosition++] = b;
    }

    public void writeBoolean(boolean b) {
        buffer[bytePosition++] = (byte) (b ? 0x01 : 0x00);
    }

    public void writeChar(char c) {
        buffer[bytePosition++] = (byte) ((c >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (c & 0xFF);
    }

    public void writeShort(short c) {
        buffer[bytePosition++] = (byte) ((c >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (c & 0xFF);
    }

    public void writeInt(int i) {
        buffer[bytePosition++] = (byte) ((i >>> 24) & 0xFF);
        buffer[bytePosition++] = (byte) ((i >>> 16) & 0xFF);
        buffer[bytePosition++] = (byte) ((i >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (i & 0xFF);
    }

    public void writeLong(long l) {
        buffer[bytePosition++] = (byte) ((l >>> 56) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 48) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 40) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 32) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 24) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 16) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (l & 0xFF);
    }

    public void writeFloat(float f) {
        writeInt(Float.floatToIntBits(f));
    }

    public void writeDouble(double d) {
        writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Ascii bits of string only. Lower 8 bits
     */
    public void writeString(String s) {
        if (s.length() > 0x7F)
            throw new RuntimeException("Tried writing a string greater than 0x7F in length.");
        buffer[bytePosition++] = (byte) s.length();
        for (int i = 0; i < s.length(); i++) {
            buffer[bytePosition++] = (byte) s.charAt(i);
        }
    }

    public boolean currentBuffersInitialized() {
        return opcodePacketData.isInitialized();
    }

    public boolean doOpcodesMatch(AbstractPacketOut abstractPacketOut) {
        return opcodePacketData.getOpcode() == abstractPacketOut.getOpcode();
    }

    public void createNewBuffers(AbstractPacketOut abstractPacketOut) {
        opcodePacketData.setOpcode(abstractPacketOut.getOpcode());
        opcodePacketData.getBuffers().add(createNewBuffer());
        opcodePacketData.setInitialized(true);
        opcodePacketData.setNumberOfRepeats(1);
    }


    public void appendBewBuffer() {
        opcodePacketData.getBuffers().add(createNewBuffer());
        opcodePacketData.setNumberOfRepeats(opcodePacketData.getNumberOfRepeats() + 1);
    }

    public int fillCurrentBuffer(AbstractPacketOut abstractPacketOut) {
        abstractPacketOut.createPacket(this);
        return bytePosition;
    }

    public void writeBuffers() throws IOException {
        boolean repeatsExist = opcodePacketData.getNumberOfRepeats() > 1;
        byte opcode = opcodePacketData.getOpcode();
        if (repeatsExist) opcode |= -0x80; // Special bit to tell the client there exist repeats

        dataOutputStream.writeByte(opcode);

        if (repeatsExist) {
            dataOutputStream.writeByte((byte) opcodePacketData.getNumberOfRepeats());
        }

        for (byte[] buffer : opcodePacketData.getBuffers()) {
            if (buffer.length != 0)
                dataOutputStream.write(buffer);
        }
        opcodePacketData.getBuffers().clear();
        opcodePacketData.setNumberOfRepeats(-1);
        opcodePacketData.setOpcode((byte) -0x80);
        opcodePacketData.setInitialized(false);
    }

    private byte[] createNewBuffer() {
        byte[] newBuffer = new byte[bytePosition];
        if (bytePosition != 0)
            System.arraycopy(buffer, 0, newBuffer, 0, bytePosition);
        Arrays.fill(buffer, (byte) 0);
        bytePosition = 0;
        return newBuffer;
    }

    public void flush() throws IOException {
        dataOutputStream.flush();
    }

    public void close() throws IOException {
        flush();
        dataOutputStream.close();
    }
}
