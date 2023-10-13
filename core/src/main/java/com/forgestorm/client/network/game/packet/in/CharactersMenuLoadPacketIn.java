package com.forgestorm.client.network.game.packet.in;

import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.character.GameCharacter;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.CHARACTERS_MENU_LOAD)
public class CharactersMenuLoadPacketIn implements PacketListener<CharactersMenuLoadPacketIn.CharacterData> {

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public CharactersMenuLoadPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        byte charactersToLoad = clientHandler.readByte();

        println(getClass(), "Total Characters: " + charactersToLoad, false, PRINT_DEBUG);
        GameCharacter[] characters = new GameCharacter[charactersToLoad];

        for (byte i = 0; i < charactersToLoad; i++) {
            // Identification
            String name = clientHandler.readString();
            byte characterId = clientHandler.readByte();

            // Location data
            String worldName = clientHandler.readString();

            // Appearance
            byte hairTexture = clientHandler.readByte();
            byte helmTexture = clientHandler.readByte();
            byte chestTexture = clientHandler.readByte();
            byte pantsTexture = clientHandler.readByte();
            byte shoesTexture = clientHandler.readByte();
            Color hairColor = new Color(clientHandler.readInt());
            Color eyeColor = new Color(clientHandler.readInt());
            Color skinColor = new Color(clientHandler.readInt());
            Color glovesColor = new Color(clientHandler.readInt());
            byte leftHandTexture = clientHandler.readByte();
            byte rightHandTexture = clientHandler.readByte();

            Appearance appearance = new Appearance();
            appearance.setHairTexture(hairTexture);
            appearance.setHelmTexture(helmTexture);
            appearance.setChestTexture(chestTexture);
            appearance.setPantsTexture(pantsTexture);
            appearance.setShoesTexture(shoesTexture);
            appearance.setHairColor(hairColor);
            appearance.setEyeColor(eyeColor);
            appearance.setSkinColor(skinColor);
            appearance.setGlovesColor(glovesColor);
            appearance.setLeftHandTexture(leftHandTexture);
            appearance.setRightHandTexture(rightHandTexture);

            characters[i] = new GameCharacter(name, characterId, worldName, appearance);

            println(getClass(), "Name: " + name, false, PRINT_DEBUG);
            println(getClass(), "CharacterID: " + characterId, false, PRINT_DEBUG);
            println(getClass(), "WorldName: " + worldName, false, PRINT_DEBUG);
            println(getClass(), "HairTexture: " + hairTexture, false, PRINT_DEBUG);
            println(getClass(), "HelmTexture: " + helmTexture, false, PRINT_DEBUG);
            println(getClass(), "ChestTexture: " + chestTexture, false, PRINT_DEBUG);
            println(getClass(), "PantsTexture: " + pantsTexture, false, PRINT_DEBUG);
            println(getClass(), "ShoesTexture: " + shoesTexture, false, PRINT_DEBUG);
            println(getClass(), "HairColor: " + hairColor, false, PRINT_DEBUG);
            println(getClass(), "EyeColor: " + eyeColor, false, PRINT_DEBUG);
            println(getClass(), "SkinColor: " + skinColor, false, PRINT_DEBUG);
            println(getClass(), "GlovesColor: " + glovesColor, false, PRINT_DEBUG);
            println(getClass(), "LeftHandTexture: " + leftHandTexture, false, PRINT_DEBUG);
            println(getClass(), "RightHandTexture: " + rightHandTexture, false, PRINT_DEBUG);
            println(PRINT_DEBUG);
        }

        return new CharacterData(characters);
    }

    @Override
    public void onEvent(CharacterData packetData) {
        clientMain.getStageHandler().getCharacterSelectMenu().characterListPacketIn(packetData.characters);
    }

    @AllArgsConstructor
    static class CharacterData extends PacketData {
        private final GameCharacter[] characters;
    }
}
