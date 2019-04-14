package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.game.rpg.CharacterClasses;
import com.valenguard.client.game.rpg.CharacterGenders;
import com.valenguard.client.game.rpg.CharacterRaces;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.util.ColorList;

import static com.valenguard.client.util.Log.println;

public class CharacterCreatorPacketOut extends AbstractClientPacketOut {

    private static final boolean PRINT_DEBUG = true;

    private final CharacterClasses characterClass;
    private final CharacterGenders characterGender;
    private final CharacterRaces characterRace;
    private final ColorList characterColor;
    private final String characterName;

    public CharacterCreatorPacketOut(CharacterClasses characterClass, CharacterGenders characterGender, CharacterRaces characterRace, ColorList characterColor, String characterName) {
        super(Opcodes.CHARACTER_CREATOR);
        this.characterClass = characterClass;
        this.characterGender = characterGender;
        this.characterRace = characterRace;
        this.characterColor = characterColor;
        this.characterName = characterName;
    }

    @Override
    void createPacket(ValenguardOutputStream write) {

        println(getClass(), "Class: " + characterClass.name(), false, PRINT_DEBUG);
        println(getClass(), "Gender: " + characterGender.name(), false, PRINT_DEBUG);
        println(getClass(), "Race: " + characterRace.name(), false, PRINT_DEBUG);
        println(getClass(), "Color: " + characterColor.name(), false, PRINT_DEBUG);
        println(getClass(), "Name: " + characterName, false, PRINT_DEBUG);

        write.writeByte(characterClass.getTypeByte());
        write.writeByte(characterGender.getTypeByte());
        write.writeByte(characterRace.getTypeByte());
        write.writeByte(characterColor.getTypeByte());
        write.writeString(characterName);
    }
}
