package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.screens.ui.actors.character.CharacterLogout;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class CharacterLogoutPacketOut extends AbstractPacketOut {

    private final CharacterLogout logoutType;

    public CharacterLogoutPacketOut(CharacterLogout logoutType) {
        super(Opcodes.CHARACTER_LOGOUT);
        this.logoutType = logoutType;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeByte(logoutType.getTypeByte());
    }
}
