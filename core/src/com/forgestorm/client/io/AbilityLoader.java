package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.forgestorm.client.game.abilities.Ability;
import com.forgestorm.client.game.abilities.AbilityType;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class AbilityLoader {

    private static final boolean PRINT_DEBUG = false;

    /**
     * Load all items from file and store in memory for quick reference.
     */
    public Map<Short, Ability> loadAbilities() {

        println(getClass(), "====== START LOADING ABILITIES ======", false, PRINT_DEBUG);

        FileHandle fileHandle = Gdx.files.internal(FilePaths.COMBAT_ABILITIES.getFilePath());
        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(fileHandle.read());

        Map<Short, Ability> combatAbilities = new HashMap<Short, Ability>();
        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            int abilityId = entry.getKey();
            Map<String, Object> itemNode = entry.getValue();

            Ability ability = new Ability();

            /*
             * Get universal item information
             */
            String name = (String) itemNode.get("name");
            AbilityType abilityType = AbilityType.valueOf((String) itemNode.get("abilityType"));
            String animation = (String) itemNode.get("animation");
            Integer damageMin = (Integer) itemNode.get("damageMin");
            Integer damageMax = (Integer) itemNode.get("damageMax");
            Integer cooldown = (Integer) itemNode.get("cooldown");
            Integer distanceMin = (Integer) itemNode.get("distanceMin");
            Integer distanceMax = (Integer) itemNode.get("distanceMax");

            ability.setAbilityId((short) abilityId);
            ability.setName(name);
            ability.setAbilityType(abilityType);
            ability.setAbilityAnimation(animation);
            ability.setDamageMin(damageMin);
            ability.setDamageMax(damageMax);
            ability.setCooldown(cooldown);
            ability.setDistanceMin(distanceMin);
            ability.setDistanceMax(distanceMax);

            combatAbilities.put((short) abilityId, ability);

            println(PRINT_DEBUG);

        }

        println(getClass(), "====== END LOADING ABILITIES ======", false, PRINT_DEBUG);
        return combatAbilities;
    }
}
