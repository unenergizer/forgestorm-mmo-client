package com.forgestorm.client.game.rpg;

import com.forgestorm.client.game.GameQuitReset;
import com.forgestorm.client.game.GameWorldSetup;

import java.util.HashMap;
import java.util.Map;

public class Skills implements GameQuitReset, GameWorldSetup {

    private final Map<SkillOpcodes, Skill> skillsMap = new HashMap<SkillOpcodes, Skill>();


    public Skills() {
        setup();
    }

    public Skill getSkill(SkillOpcodes opcode) {
        return skillsMap.get(opcode);
    }

    @Override
    public void setup() {
        skillsMap.put(SkillOpcodes.MINING, new Skill((SkillOpcodes.MINING)));
        skillsMap.put(SkillOpcodes.MELEE, new Skill(SkillOpcodes.MELEE));
    }

    @Override
    public void gameQuitReset() {
        skillsMap.clear();

        setup();
    }
}
