package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.abilities.Ability;
import com.forgestorm.client.game.abilities.AbilityType;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class AbilityLoader extends AsynchronousAssetLoader<AbilityLoader.AbilityDataWrapper, AbilityLoader.AbilityParameter> {

    static class AbilityParameter extends AssetLoaderParameters<AbilityDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private AbilityDataWrapper abilityDataWrapper = null;

    AbilityLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, AbilityParameter parameter) {
        abilityDataWrapper = null;
        abilityDataWrapper = new AbilityDataWrapper();
        println(getClass(), "====== START LOADING ABILITIES ======", false, PRINT_DEBUG);

        abilityDataWrapper.setCombatAbilitiesMap(new HashMap<Short, Ability>());
        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(file.read());

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

            abilityDataWrapper.combatAbilitiesMap.put((short) abilityId, ability);

            println(PRINT_DEBUG);
        }

        println(getClass(), "====== END LOADING ABILITIES ======", false, PRINT_DEBUG);
    }

    @Override
    public AbilityDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, AbilityParameter parameter) {
        return abilityDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AbilityParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public class AbilityDataWrapper {
        private Map<Short, Ability> combatAbilitiesMap = null;
    }
}
