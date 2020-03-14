package com.valenguard.client.game.abilities;

public class AbilityManager {
    // TODO: REDO TO USE ITEMSTACKS!
    //  When redoing this, make sure to search for "AbilityManager"
    //  in the project to make sure we fix and enable everything.
//    public class AbilityManager implements GameQuitReset {

//    private Map<Short, Cooldown> cooldowns = new HashMap<Short, Cooldown>();
//
//    private Map<Short, Ability> combatAbilities;
//
//    public AbilityManager() {
//        combatAbilities = new AbilityLoader().loadAbilities();
//    }
//
//    public void toggleAbility(short abilityID, GameButtonBar gameButtonBar, VisImageButton button) {
//        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
//        if (playerClient.getTargetEntity() == null) {
//            ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You need to select a target first.");
//            return;
//        }
//
//        Ability ability = combatAbilities.get(abilityID);
//        if (cooldowns.containsKey(abilityID)) return;
//
//        MovingEntity targetEntity = EntityManager.getInstance().getPlayerClient().getTargetEntity();
//
//        Location playerLocation = playerClient.getFutureMapLocation();
//        Location targetLocation = targetEntity.getFutureMapLocation();
//
//        if (ability.getDistanceMin() != null && ability.getDistanceMax() != null) {
//
//            int distanceAway = playerLocation.getDistanceAway(targetLocation);
//            if (distanceAway < ability.getDistanceMin()) {
//                ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You are too close to " + targetEntity.getEntityName() + " to do this.");
//                return;
//            } else if (distanceAway > ability.getDistanceMax()) {
//                ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You are too far away from " + targetEntity.getEntityName() + " to do this.");
//                return;
//            }
//        } else {
//            if (!playerLocation.isWithinDistance(targetLocation, (short) 1)) { // TODO: This should be based on weapon distance reach
//                ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[RED]You are too far away from " + targetEntity.getEntityName() + " to do this.");
//                return;
//            }
//        }
//
//        new AbilityRequestPacketOut(abilityID, EntityManager.getInstance().getPlayerClient().getTargetEntity()).sendPacket();
//
//        // Set cooldown time * 60 frames a second
//        cooldowns.put(abilityID, new Cooldown(gameButtonBar, button, ability.getCooldown() * 60));
//    }
//
//    public void updateCooldowns() {
//        Iterator<Map.Entry<Short, Cooldown>> iterator = cooldowns.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Short, Cooldown> entry = iterator.next();
//            Cooldown cooldown = entry.getValue();
//            int cooldownRemaining = cooldown.remaining - 1;
//            if (cooldownRemaining <= 0) {
//                cooldown.gameButtonBar.resetButton(cooldown.button);
//                iterator.remove();
//            } else {
//                // initialAmount = ability.getCooldown();
//                //
//                // percentOfBarToDisplay = cooldownRemaining / initialAmount
//
//                float displayPercent = (float) cooldownRemaining / cooldown.initCooldown;
//                cooldown.remaining = cooldownRemaining;
//
//                cooldown.gameButtonBar.setCoolingDown(cooldown.button);
//            }
//        }
//    }
//
//    @Override
//    public void gameQuitReset() {
//        cooldowns.clear();
//    }
//
//    private class Cooldown {
//        private final GameButtonBar gameButtonBar;
//        private final VisImageButton button;
//        private int remaining;
//        private int initCooldown;
//
//        Cooldown(GameButtonBar gameButtonBar, VisImageButton button, int time) {
//            this.gameButtonBar = gameButtonBar;
//            this.button = button;
//            remaining = time;
//            initCooldown = time;
//        }
//    }
}
