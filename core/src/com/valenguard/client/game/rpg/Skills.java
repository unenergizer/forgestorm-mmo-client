package com.valenguard.client.game.rpg;

import lombok.Getter;
import lombok.Setter;

public class Skills {

    @Getter
    @Setter
    private byte level;

    @Getter
    @Setter
    private byte boostedLevel;

    private byte experience;

    //   public final Skills MINING = new Skills();

    public void addExperience(int experience) {

    }
}
