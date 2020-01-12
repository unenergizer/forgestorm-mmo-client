package com.valenguard.client.game.abilities;

import com.kotcrab.vis.ui.widget.VisImageButton;
import com.valenguard.client.game.GameQuitReset;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.game.AbilityBar;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.io.AbilityLoader;
import com.valenguard.client.network.game.packet.out.AbilityRequestPacketOut;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AbilityManager implements GameQuitReset {

    private Map<Short, Cooldown> cooldowns = new HashMap<Short, Cooldown>();

    private Map<Short, Ability> combatAbilities;

    public AbilityManager() {
        combatAbilities = new AbilityLoader().loadAbilities();
    }

    public void toggleAbility(short abilityID, AbilityBar abilityBar, VisImageButton button) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        if (playerClient.getTargetEntity() == null) {
            ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You need to select a target first.");
            return;
        }

        Ability ability = combatAbilities.get(abilityID);
        if (cooldowns.containsKey(abilityID)) return;

        MovingEntity targetEntity = EntityManager.getInstance().getPlayerClient().getTargetEntity();

        Location playerLocation = playerClient.getFutureMapLocation();
        Location targetLocation = targetEntity.getFutureMapLocation();

        if (ability.getDistanceMin() != null && ability.getDistanceMax() != null) {

            int distanceAway = playerLocation.getDistanceAway(targetLocation);
            if (distanceAway < ability.getDistanceMin()) {
                ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You are too close to " + targetEntity.getEntityName() + " to do this.");
                return;
            } else if (distanceAway > ability.getDistanceMax()) {
                ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You are too far away from " + targetEntity.getEntityName() + " to do this.");
                return;
            }
        } else {
            if (!playerLocation.isWithinDistance(targetLocation, (short) 1)) { // TODO: This should be based on weapon distance reach
                ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You are too far away from " + targetEntity.getEntityName() + " to do this.");
                return;
            }
        }

        new AbilityRequestPacketOut(abilityID, EntityManager.getInstance().getPlayerClient().getTargetEntity()).sendPacket();

        // Set cooldown time * 60 frames a second
        cooldowns.put(abilityID, new Cooldown(abilityBar, button, ability.getCooldown() * 60));
    }

    public void updateCooldowns() {
        Iterator<Map.Entry<Short, Cooldown>> iterator = cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Short, Cooldown> entry = iterator.next();
            Cooldown cooldown = entry.getValue();
            int cooldownRemaining = cooldown.remaining - 1;
            if (cooldownRemaining <= 0) {
                cooldown.abilityBar.resetButton(cooldown.button);
                iterator.remove();
            } else {
                // initialAmount = ability.getCooldown();
                //
                // percentOfBarToDisplay = cooldownRemaining / initialAmount

                float displayPercent = (float) cooldownRemaining / cooldown.initCooldown;
                cooldown.remaining = cooldownRemaining;

                cooldown.abilityBar.setCoolingDown(cooldown.button);
            }
        }
    }

    @Override
    public void reset() {
        cooldowns.clear();
    }

    private class Cooldown {
        private final AbilityBar abilityBar;
        private final VisImageButton button;
        private int remaining;
        private int initCooldown;

        Cooldown(AbilityBar abilityBar, VisImageButton button, int time) {
            this.abilityBar = abilityBar;
            this.button = button;
            remaining = time;
            initCooldown = time;
        }
    }
}
