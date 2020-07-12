package com.forgestorm.client.network.game.packet.out;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("unused")
public class ForgeStormOutputStream {

    private final DataOutputStream dataOutputStream;

    public ForgeStormOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    private int bytePosition = 0;

    private OpcodePacketData opcodePacketData = new OpcodePacketData();

    // The current buffer being worked on.
    private byte[] buffer = new byte[200];

    void writeByte(byte b) {
        buffer[bytePosition++] = b;
    }

    void writeBoolean(boolean b) {
        buffer[bytePosition++] = (byte) (b ? 0x01 : 0x00);
    }

    void writeChar(char c) {
        buffer[bytePosition++] = (byte) ((c >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (c & 0xFF);
    }

    void writeShort(short c) {
        buffer[bytePosition++] = (byte) ((c >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (c & 0xFF);
    }

    void writeInt(int i) {
        buffer[bytePosition++] = (byte) ((i >>> 24) & 0xFF);
        buffer[bytePosition++] = (byte) ((i >>> 16) & 0xFF);
        buffer[bytePosition++] = (byte) ((i >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (i & 0xFF);
    }

    void writeLong(long l) {
        buffer[bytePosition++] = (byte) ((l >>> 56) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 48) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 40) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 32) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 24) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 16) & 0xFF);
        buffer[bytePosition++] = (byte) ((l >>> 8) & 0xFF);
        buffer[bytePosition++] = (byte) (l & 0xFF);
    }

    void writeFloat(float f) {
        writeInt(Float.floatToIntBits(f));
    }

    void writeDouble(double d) {
        writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Ascii bits of string only. Lower 8 bits
     */
    void writeString(String s) {
        if (s.length() > 0x7F)
            throw new RuntimeException("Tried writing a string greater than 0x7F in length.");
        buffer[bytePosition++] = (byte) s.length();
        for (int i = 0; i < s.length(); i++) {
            buffer[bytePosition++] = (byte) s.charAt(i);
        }
    }

    boolean currentBuffersInitialized() {
        return opcodePacketData.isInitialized();
    }

    boolean doOpcodesMatch(AbstractClientPacketOut abstractClientPacketOut) {
        return opcodePacketData.getOpcode() == abstractClientPacketOut.getOpcode();
    }

    void createNewBuffers(AbstractClientPacketOut abstractClientPacketOut) {
        opcodePacketData.setOpcode(abstractClientPacketOut.getOpcode());
        opcodePacketData.getBuffers().add(createNewBuffer());
        opcodePacketData.setInitialized(true);
        opcodePacketData.setNumberOfRepeats(1);
    }


    void appendBewBuffer() {
        opcodePacketData.getBuffers().add(createNewBuffer());
        opcodePacketData.setNumberOfRepeats(opcodePacketData.getNumberOfRepeats() + 1);
    }

    public int fillCurrentBuffer(AbstractClientPacketOut abstractClientPacketOut) {
        abstractClientPacketOut.createPacket(this);
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
        dataOutputStream.close();
    }
}
