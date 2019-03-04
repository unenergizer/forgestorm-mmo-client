package com.valenguard.client.game.rpg;

import com.valenguard.client.game.entities.EntityManager;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class Skill {

    private static final boolean PRINT_DEBUG = false;
    private final SkillOpcodes skillOpcodes;

    public Skill(SkillOpcodes skillOpcodes) {
        this.skillOpcodes = skillOpcodes;
    }

    private boolean initialized = false;

    @Getter
    private byte level;

    private byte boostedLevel;

    @Getter
    private int experience;

    private int var1 = 500;
    private double var2 = 6.5;
    private double var3 = 3.5;

    private int expOffSet = getExperience(1, true);

    public void addExperience(int experience) {
        this.experience += experience;
        // Calculate level from experience
        int previousLevel = level;

        println(getClass(), "Level Before = " + level, false, PRINT_DEBUG);

        level = getLevelFromExperience(this.experience);

        println(getClass(), "Total Exp = " + this.experience, false, PRINT_DEBUG);
        println(getClass(), "Level = " + level, false, PRINT_DEBUG);

        if (!initialized) {
            println(getClass(), "<" + skillOpcodes.name() + "> Set the player's experience to: " + experience, false, PRINT_DEBUG);
            initialized = true;
            return;
        }

        // The player has leveled up
        if (previousLevel != level) {
            println(getClass(), "The player has leveled up!", false, PRINT_DEBUG);

            // TODO: TELL TO SHOW MESSAGE
            EntityManager.getInstance().getPlayerClient().setShowLevelUpMessage(true);
        }
        // check if they have gained a level and do skill level animation effect / send chat msg ect..
    }

    private byte getLevelFromExperience(int exp) {
        for (byte i = 1; i <= 100; i++) {
            if (exp >= getExperience(i, false) && exp < getExperience(i + 1, false)) {
                return i;
            }
        }
        return 0;
    }

    private int getExperience(int level, boolean getOffSet) {
        int points = 0;
        int output;

        int maxLevel = 100;
        for (int lvl = 1; lvl <= maxLevel; lvl++) {
            points += Math.floor(var1 * Math.pow(2, lvl / var2));

            output = (int) Math.floor(points / var3);

            if (lvl == level) {
                return getOffSet ? output : output - expOffSet;
            }
        }
        return 0;
    }

}
