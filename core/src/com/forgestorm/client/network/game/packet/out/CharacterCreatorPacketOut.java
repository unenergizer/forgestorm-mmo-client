package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class CharacterCreatorPacketOut extends AbstractPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final String characterName;
    private final byte hairTexture;
    private final byte hairColor;
    private final byte eyeColor;
    private final byte skinColor;

    public CharacterCreatorPacketOut(String characterName, byte hairTexture, byte hairColor, byte eyeColor, byte skinColor) {
        super(Opcodes.CHARACTER_CREATOR);
        this.characterName = characterName;
        this.hairTexture = hairTexture;
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
        this.skinColor = skinColor;
    }

    @Override
    public void createPacket(GameOutputStream write) {

        println(getClass(), "Name: " + characterName, false, PRINT_DEBUG);
        println(getClass(), "HairTexture: " + hairTexture, false, PRINT_DEBUG);
        println(getClass(), "HairColor: " + hairColor + " (Ordinal)", false, PRINT_DEBUG);
        println(getClass(), "EyeColor: " + eyeColor + " (Ordinal)", false, PRINT_DEBUG);
        println(getClass(), "SkinColor: " + skinColor + " (Ordinal)", false, PRINT_DEBUG);

        write.writeString(characterName);
        write.writeByte(hairTexture);
        write.writeByte(hairColor);
        write.writeByte(eyeColor);
        write.writeByte(skinColor);
    }
}
