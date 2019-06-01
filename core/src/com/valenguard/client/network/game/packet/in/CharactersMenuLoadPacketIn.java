package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.character.CharacterSelectMenu;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.CHARACTERS_MENU_LOAD)
public class CharactersMenuLoadPacketIn implements PacketListener<CharactersMenuLoadPacketIn.CharacterData> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        byte charactersToLoad = clientHandler.readByte();

        println(getClass(), "Total Characters: " + charactersToLoad, false, PRINT_DEBUG);
        GameCharacter[] characters = new GameCharacter[charactersToLoad];

        for (byte i = 0; i < charactersToLoad; i++) {
            String name = clientHandler.readString();
            byte characterId = clientHandler.readByte();
            byte hairTexture = clientHandler.readByte();
            int hairColor = clientHandler.readInt();
            int eyeColor = clientHandler.readInt();
            int skinColor = clientHandler.readInt();

            characters[i] = new GameCharacter(name, characterId, hairTexture, hairColor, eyeColor, skinColor);

            println(getClass(), "Name: " + name, false, PRINT_DEBUG);
            println(getClass(), "CharacterID: " + characterId, false, PRINT_DEBUG);
            println(getClass(), "HairTexture: " + hairTexture, false, PRINT_DEBUG);
            println(getClass(), "HairColor: " + hairColor, false, PRINT_DEBUG);
            println(getClass(), "EyeColor: " + eyeColor, false, PRINT_DEBUG);
            println(getClass(), "SkinColor: " + skinColor, false, PRINT_DEBUG);
        }

        return new CharacterData(characters);
    }

    @Override
    public void onEvent(CharacterData packetData) {
        CharacterSelectMenu characterSelectMenu = ActorUtil.getStageHandler().getCharacterSelectMenu();
        characterSelectMenu.reset();
        for (GameCharacter character : packetData.characters) {
            characterSelectMenu.addCharacterButton(character);
        }
    }

    @AllArgsConstructor
    class CharacterData extends PacketData {
        private GameCharacter[] characters;
    }

    @Getter
    @AllArgsConstructor
    public class GameCharacter {
        private final String name;
        private final byte characterId;
        private final byte hairTexture;
        private final int hairColor;
        private final int eyeColor;
        private final int skinColor;
    }

}
