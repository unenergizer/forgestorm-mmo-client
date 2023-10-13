package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.character.CharacterCreation;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.CharacterCreatorResponses;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.CHARACTER_CREATOR_ERROR)
public class CharacterCreatorPacketIn implements PacketListener<CharacterCreatorPacketIn.CharacterCreatorError> {

    private final ClientMain clientMain;

    public CharacterCreatorPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new CharacterCreatorError(CharacterCreatorResponses.getCharacterErrorType(clientHandler.readByte()));
    }

    @Override
    public void onEvent(CharacterCreatorError packetData) {
        CharacterCreation characterCreation = clientMain.getStageHandler().getCharacterCreation();
        switch (packetData.characterCreatorResponses) {
            case SUCCESS:
                characterCreation.creationSuccess();
                break;
            case FAIL_BLACKLIST_NAME:
            case FAIL_NAME_TAKEN:
            case FAIL_TOO_MANY_CHARACTERS:
                characterCreation.creationFail(packetData.characterCreatorResponses);
                break;
        }
    }

    @AllArgsConstructor
    static class CharacterCreatorError extends PacketData {
        private final CharacterCreatorResponses characterCreatorResponses;
    }
}
