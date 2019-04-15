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

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        byte charactersToLoad = clientHandler.readByte();

        println(getClass(), "Total Characters: " + charactersToLoad);
        GameCharacter[] characters = new GameCharacter[charactersToLoad];

        for (byte i = 0; i < charactersToLoad; i++) {
            String name = clientHandler.readString();
            byte characterId = clientHandler.readByte();
            short bodyId = clientHandler.readShort();
            short headId = clientHandler.readShort();
            byte colorId = clientHandler.readByte();

            characters[i] = new GameCharacter(name, characterId, bodyId, headId, colorId);

            println(getClass(), "Name: " + name);
            println(getClass(), "CharacterID: " + characterId);
            println(getClass(), "BodyID: " + bodyId);
            println(getClass(), "HeadID: " + headId);
            println(getClass(), "ColorID: " + colorId);
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
        private final short bodyId;
        private final short headId;
        private final byte colorId;
    }

}
