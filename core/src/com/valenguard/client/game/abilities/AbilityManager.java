package com.valenguard.client.game.abilities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.ClientMain;
import com.valenguard.client.game.GameQuitReset;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackSlot;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.io.AbilityLoader;
import com.valenguard.client.io.FileManager;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.out.AbilityRequestPacketOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.valenguard.client.util.Log.println;

public class AbilityManager implements GameQuitReset, Disposable {

    private static final boolean PRINT_DEBUG = false;

    private TextureAtlas textureAtlas;

    private Map<Short, Cooldown> cooldowns = new HashMap<Short, Cooldown>();

    private Map<Short, Ability> combatAbilities;

    private List<AbilityAnimation> abilityAnimationList = new ArrayList<AbilityAnimation>();

    public AbilityManager() {
        combatAbilities = new AbilityLoader().loadAbilities();

        // Load Atlas
        FileManager fileManager = ClientMain.getInstance().getFileManager();
        fileManager.loadAtlas(GameAtlas.PIXEL_FX);
        textureAtlas = fileManager.getAtlas(GameAtlas.PIXEL_FX);
    }

    public void toggleAbility(ItemStackSlot sourceSlot, ItemStack itemStack) {
        short abilityID = (short) (int) itemStack.getSkillID();
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

        // Play ability animation instantly for client (regardless of successful hit
        abilityAnimationList.add(new AbilityAnimation(targetEntity, ability));

        // Set cooldown time * 60 frames a second
        cooldowns.put(abilityID, new Cooldown(sourceSlot, ability.getCooldown() * 60));
    }

    public void updateCooldowns() {
        Iterator<Map.Entry<Short, Cooldown>> iterator = cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Short, Cooldown> entry = iterator.next();
            Cooldown cooldown = entry.getValue();

            if (cooldown.remaining == cooldown.initCooldown) {
                cooldown.sourceSlot.activateItemStack();
            } else if (cooldown.remaining <= 0) {
                cooldown.sourceSlot.resetItemStack();
                iterator.remove();
            }

            cooldown.sourceSlot.updateCountdown(cooldown.remaining);
            cooldown.remaining = cooldown.remaining - 1;
        }
    }

    public void drawAnimation(float deltaTime, SpriteBatch spriteBatch) {
        Iterator<AbilityAnimation> iterator = abilityAnimationList.iterator();

        while (iterator.hasNext()) {
            AbilityAnimation abilityAnimation = iterator.next();
            abilityAnimation.stateTime += deltaTime;

            TextureRegion currentFrame = abilityAnimation.animation.getKeyFrame(abilityAnimation.stateTime, true);

            // TODO: Alignment of animations on top of entities is not correct...
            int x = (int) (abilityAnimation.targetEntity.getDrawX() + (16 / 2) - (currentFrame.getRegionWidth() / 2));
            int y = (int) (abilityAnimation.targetEntity.getDrawY() + (16 / 2) - (currentFrame.getRegionHeight() / 2));

            spriteBatch.draw(currentFrame, x, y);

            if (abilityAnimation.animation.isAnimationFinished(abilityAnimation.stateTime)) {
                iterator.remove();
                println(getClass(), "Animation finished. Removing..", false , PRINT_DEBUG);
                println(getClass(), "Animations left: " + abilityAnimationList.size(), false , PRINT_DEBUG);
            }
        }
    }

    @Override
    public void gameQuitReset() {
        cooldowns.clear();
        abilityAnimationList.clear();
    }

    @Override
    public void dispose() {
        textureAtlas.dispose();
    }

    private class AbilityAnimation {

        private final MovingEntity targetEntity;
        private final Animation<TextureRegion> animation;

        private float stateTime = 0f;

        private AbilityAnimation(MovingEntity targetEntity, Ability ability) {
            this.targetEntity = targetEntity;
            this.animation = new Animation<TextureRegion>(.3f, textureAtlas.findRegions(ability.getAbilityAnimation()), Animation.PlayMode.NORMAL);
        }
    }

    private class Cooldown {
        private final ItemStackSlot sourceSlot;
        private int remaining;

        private int initCooldown;

        Cooldown(ItemStackSlot sourceSlot, int time) {
            this.sourceSlot = sourceSlot;
            remaining = time;
            initCooldown = time;
        }

    }
}
