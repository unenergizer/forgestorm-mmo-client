package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.network.game.shared.Opcodes;

public class CharacterDeletePacketOut extends AbstractClientPacketOut {

    private final byte characterListIndex;

    public CharacterDeletePacketOut(byte characterListIndex) {
        super(Opcodes.CHARACTER_DELETE);
        this.characterListIndex = characterListIndex;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeByte(characterListIndex);
    }
}
