package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.network.game.shared.Opcodes;

import static com.valenguard.client.util.Log.println;

public class CharacterSelectPacketOut extends AbstractClientPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final byte characterId;

    public CharacterSelectPacketOut(final byte characterId) {
        super(Opcodes.CHARACTER_SELECT);
        this.characterId = characterId;
    }

    @Override
    void createPacket(ValenguardOutputStream write) {
        println(getClass(), "Selecting character ID: " + characterId, false, PRINT_DEBUG);
        write.writeByte(characterId);
    }
}
