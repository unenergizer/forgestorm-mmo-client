package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.screens.ui.actors.character.CharacterLogout;
import com.forgestorm.client.network.game.shared.Opcodes;

public class CharacterLogoutPacketOut extends AbstractClientPacketOut {

    private final CharacterLogout logoutType;

    public CharacterLogoutPacketOut(CharacterLogout logoutType) {
        super(Opcodes.CHARACTER_LOGOUT);
        this.logoutType = logoutType;
    }

    @Override
    void createPacket(ForgeStormOutputStream write) {
        write.writeByte(logoutType.getTypeByte());
    }
}
