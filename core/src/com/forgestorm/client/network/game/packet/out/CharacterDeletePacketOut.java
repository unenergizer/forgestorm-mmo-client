package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.network.game.shared.Opcodes;

public class CharacterDeletePacketOut extends AbstractClientPacketOut {

    private final byte characterListIndex;

    public CharacterDeletePacketOut(byte characterListIndex) {
        super(Opcodes.CHARACTER_DELETE);
        this.characterListIndex = characterListIndex;
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        write.writeByte(characterListIndex);
    }
}
