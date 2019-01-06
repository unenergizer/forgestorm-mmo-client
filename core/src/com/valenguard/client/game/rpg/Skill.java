package com.valenguard.client.game.rpg;

public class Skill {

    private boolean initialized = false;

    private byte level;

    private byte boostedLevel;

    private byte experience;

    public void addExperience(int experience) {
        this.experience += experience;
        // Calculate level from experience
        level = 1;

        if (!initialized) {
            System.out.println("Set the player's experience to: " + experience);
            initialized = true;
            return;
        }
        // check if they have gained a level and do skill level animation effect / send chat msg ect..
    }

}
