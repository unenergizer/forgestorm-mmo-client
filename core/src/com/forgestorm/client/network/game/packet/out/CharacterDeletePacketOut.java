package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class CharacterDeletePacketOut extends AbstractPacketOut {

    private final byte characterListIndex;

    public CharacterDeletePacketOut(byte characterListIndex) {
        super(Opcodes.CHARACTER_DELETE);
        this.characterListIndex = characterListIndex;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeByte(characterListIndex);
    }
}
