package com.valenguard.client.game.rpg;

import java.util.HashMap;
import java.util.Map;

public class Skills {

    private final Map<Byte, Skill> skillsMap = new HashMap<Byte, Skill>();

    public final Skill MINING = new Skill();

    public Skills() {
        skillsMap.put(SkillOpcodes.MINING, MINING);
    }

    public Skill getSkill(byte opcode) {
        return skillsMap.get(opcode);
    }
}
