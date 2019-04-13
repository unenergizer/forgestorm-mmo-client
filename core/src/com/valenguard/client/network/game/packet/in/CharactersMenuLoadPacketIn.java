package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.CHARACTERS_MENU_LOAD)
public class CharactersMenuLoadPacketIn implements PacketListener<CharactersMenuLoadPacketIn.CharacterData> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        byte charactersToLoad = clientHandler.readByte();

        Character[] characters = new Character[charactersToLoad];

        for (byte i = 0; i < charactersToLoad; i++) {
            String name = clientHandler.readString();
            byte characterId = clientHandler.readByte();
            characters[i] = new Character(name, characterId);
        }

        return new CharacterData(characters);
    }

    @Override
    public void onEvent(CharacterData packetData) {
        for (Character character : packetData.characters) {
            ActorUtil.getStageHandler().getCharacterSelectMenu().addCharacterButton(character.name, character.characterId);
        }
    }

    @AllArgsConstructor
    class CharacterData extends PacketData {
        private Character[] characters;
    }

    @AllArgsConstructor
    private class Character {
        private final String name;
        private final byte characterId;
    }

}
