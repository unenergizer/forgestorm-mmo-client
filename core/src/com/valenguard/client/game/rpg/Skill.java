package com.valenguard.client.game.rpg;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.event.ExperienceUpdateEvent;
import com.valenguard.client.game.world.entities.EntityManager;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class Skill {

    private static final boolean PRINT_DEBUG = false;
    private final SkillOpcodes skillOpcodes;

    Skill(SkillOpcodes skillOpcodes) {
        this.skillOpcodes = skillOpcodes;
    }

    private boolean initialized = false;

    @Getter
    private byte currentLevel;

    @Getter
    private int experience;

    private static final int var1 = 500;
    private static final double var2 = 6.5;
    private static final double var3 = 3.5;

    private int expOffSet = getExperience(1, true);

    public void addExperience(int expGained) {

        println(PRINT_DEBUG);
        println(getClass(), "-----[" + skillOpcodes + "]---------------------------------------------", false, PRINT_DEBUG);
        println(getClass(), "Experience Gained = " + expGained, false, PRINT_DEBUG);

        experience += expGained;
        // Calculate level from experience
        int previousLevel = currentLevel;

        println(getClass(), "Level Before = " + currentLevel, false, PRINT_DEBUG);

        currentLevel = getLevelFromExperience(experience);
        int currentLevelExp = getExperience(currentLevel, false);

        println(getClass(), "Total Exp = " + experience, false, PRINT_DEBUG);
        println(getClass(), "Level = " + currentLevel, false, PRINT_DEBUG);
        println(getClass(), "LevelExp: " + currentLevelExp, false, PRINT_DEBUG);

        // Figure out what the next level is exp wise
        int nextLevel = currentLevel + 1;
        int nextLevelExp = getExperience(nextLevel, false);

        println(getClass(), "NextLevel = " + nextLevel, false, PRINT_DEBUG);
        println(getClass(), "NextLevelExp = " + nextLevelExp, false, PRINT_DEBUG);

        float barPercent = getBarPercent(experience, currentLevel, currentLevelExp, nextLevel, nextLevelExp);

        // Update UI
        if (skillOpcodes == SkillOpcodes.MELEE) {
            Valenguard.getInstance().getStageHandler().getExperienceBar().updateExp(barPercent, experience, currentLevel, nextLevelExp);
        }

        // Update UI values
        ExperienceUpdateEvent experienceUpdateEvent = new ExperienceUpdateEvent(skillOpcodes, currentLevel);
        for (Actor actor : ActorUtil.getStage().getActors()) {
            actor.fire(experienceUpdateEvent);
        }

        // The player has leveled up
        if (previousLevel != currentLevel && initialized) {
            println(getClass(), "The player has leveled up!", false, PRINT_DEBUG);

            // TODO: TELL TO SHOW MESSAGE
            EntityManager.getInstance().getPlayerClient().setShowLevelUpMessage(true);

            // check if they have gained a level and do skill level animation effect / send chat msg ect..
            Valenguard.getInstance().getStageHandler().getChatWindow().appendChatMessage("[GREEN]You are now level " + currentLevel);
        }

        if (!initialized) {
            println(getClass(), "<" + skillOpcodes.name() + "> Set the player's experience to: " + expGained, false, PRINT_DEBUG);
            initialized = true;
            return;
        }

        println(getClass(), "-------------------------------------------------------------", false, PRINT_DEBUG);
    }

    public float getBarPercent(int currentExp, int currentLevel, int currentLevelExp, int nextLevel, int nextLevelExp) {
        float percent = (float) (nextLevelExp - currentExp) / (float) (nextLevelExp - currentLevelExp);

        println(getClass(), "CurrentExp: " + currentExp
                + " [CurrentLevel: " + currentLevel
                + ", CurrentLevelExp: " + currentLevelExp
                + "] [NextLevel: " + nextLevel
                + ", NextLevelExp: " + nextLevelExp
                + "] Percent: " + percent, false, PRINT_DEBUG);

        return (1 - percent) * 100;
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
