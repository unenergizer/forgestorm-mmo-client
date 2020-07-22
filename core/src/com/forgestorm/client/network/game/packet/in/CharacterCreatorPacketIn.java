package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.character.CharacterCreation;
import com.forgestorm.client.network.game.CharacterCreatorResponses;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.CHARACTER_CREATOR_ERROR)
public class CharacterCreatorPacketIn implements PacketListener<CharacterCreatorPacketIn.CharacterCreatorError> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new CharacterCreatorError(CharacterCreatorResponses.getCharacterErrorType(clientHandler.readByte()));
    }

    @Override
    public void onEvent(CharacterCreatorError packetData) {
        CharacterCreation characterCreation = ClientMain.getInstance().getStageHandler().getCharacterCreation();
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
    class CharacterCreatorError extends PacketData {
        private final CharacterCreatorResponses characterCreatorResponses;
    }
}
