package com.valenguard.client.game.rpg;

import java.util.HashMap;
import java.util.Map;

public class Skills {

    private final Map<SkillOpcodes, Skill> skillsMap = new HashMap<SkillOpcodes, Skill>();

    private final Skill MINING = new Skill(SkillOpcodes.MINING);
    private final Skill MELEE = new Skill(SkillOpcodes.MELEE);

    public Skills() {
        skillsMap.put(SkillOpcodes.MINING, MINING);
        skillsMap.put(SkillOpcodes.MELEE, MELEE);
    }

    public Skill getSkill(SkillOpcodes opcode) {
        return skillsMap.get(opcode);
    }
}
