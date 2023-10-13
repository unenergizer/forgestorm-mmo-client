package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class CharacterSelectPacketOut extends AbstractPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final byte characterId;

    public CharacterSelectPacketOut(ClientMain clientMain, final byte characterId) {
        super(clientMain, Opcodes.CHARACTER_SELECT);
        this.characterId = characterId;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        println(getClass(), "Selecting character ID: " + characterId, false, PRINT_DEBUG);
        write.writeByte(characterId);
    }
}
