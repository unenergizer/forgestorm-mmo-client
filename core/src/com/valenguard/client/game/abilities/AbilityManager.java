package com.valenguard.client.game.abilities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.valenguard.client.game.screens.ui.actors.game.AbilityBar;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.io.AbilityLoader;
import com.valenguard.client.network.game.packet.out.AbilityRequestPacketOut;

import java.util.Map;

public class AbilityManager {

    private Map<Short, Ability> combatAbilities;

    public AbilityManager() {
        combatAbilities = new AbilityLoader().loadAbilities();
    }

    public void toggleAbility(short abilityID, AbilityBar abilityBar, Actor actor) {
        if (combatAbilities.get(abilityID).getCooldown() == null) return;

        new AbilityRequestPacketOut(abilityID, EntityManager.getInstance().getPlayerClient().getTargetEntity()).sendPacket();
        // do something

    }
}
