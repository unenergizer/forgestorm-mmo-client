package com.valenguard.client.network.game.packet.out;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
class OpcodePacketData {
    private boolean initialized = false;
    private byte opcode;
    private int numberOfRepeats;
    private List<byte[]> buffers = new ArrayList<byte[]>();
}
