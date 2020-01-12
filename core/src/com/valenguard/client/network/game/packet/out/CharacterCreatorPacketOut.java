package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.game.rpg.CharacterClasses;
import com.valenguard.client.game.rpg.CharacterGenders;
import com.valenguard.client.game.rpg.CharacterRaces;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.util.color.LibGDXColorList;

import static com.valenguard.client.util.Log.println;

public class CharacterCreatorPacketOut extends AbstractClientPacketOut {

    private static final boolean PRINT_DEBUG = false;

    //    private final CharacterClasses characterClass;
//    private final CharacterGenders characterGender;
//    private final CharacterRaces characterRace;
//    private final LibGDXColorList characterColor;
    private final String characterName;

    public CharacterCreatorPacketOut(String characterName) {
        super(Opcodes.CHARACTER_CREATOR);
//        this.characterClass = characterClass;
//        this.characterGender = characterGender;
//        this.characterRace = characterRace;
//        this.characterColor = characterColor;
        this.characterName = characterName;
    }

    @Override
    void createPacket(ValenguardOutputStream write) {

//        println(getClass(), "Class: " + characterClass.name(), false, PRINT_DEBUG);
//        println(getClass(), "Gender: " + characterGender.name(), false, PRINT_DEBUG);
//        println(getClass(), "Race: " + characterRace.name(), false, PRINT_DEBUG);
//        println(getClass(), "Color: " + characterColor.name(), false, PRINT_DEBUG);
        println(getClass(), "Name: " + characterName, false, PRINT_DEBUG);

        write.writeByte(CharacterClasses.FIGHTER.getTypeByte());
        write.writeByte(CharacterGenders.MALE.getTypeByte());
        write.writeByte(CharacterRaces.HUMAN.getTypeByte());
        write.writeByte(LibGDXColorList.PLAYER_DEFAULT.getTypeByte());
        write.writeString(characterName);
    }
}
