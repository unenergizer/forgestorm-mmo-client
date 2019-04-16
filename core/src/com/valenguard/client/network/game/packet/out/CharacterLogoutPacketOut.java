package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.game.screens.ui.actors.character.CharacterLogout;
import com.valenguard.client.network.game.shared.Opcodes;

public class CharacterLogoutPacketOut extends AbstractClientPacketOut {

    private final CharacterLogout logoutType;

    public CharacterLogoutPacketOut(CharacterLogout logoutType) {
        super(Opcodes.CHARACTER_LOGOUT);
        this.logoutType = logoutType;
    }

    @Override
    void createPacket(ValenguardOutputStream write) {
        write.writeByte(logoutType.getTypeByte());
    }
}
