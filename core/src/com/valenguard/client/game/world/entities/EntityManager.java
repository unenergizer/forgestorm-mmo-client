package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.io.type.GameAtlas;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

public class EntityManager implements Disposable {

    private EntityManager() {
    }

    @Getter
    @Setter
    private PlayerClient playerClient;

    @SuppressWarnings("LibGDXStaticResource")
    @Getter
    private static final EntityManager instance = new EntityManager();

    //  EntityId -> Entity
    @Getter
    private Map<Short, Player> playerEntityList = new HashMap<Short, Player>();

    @Getter
    private Map<Short, AiEntity> aiEntityList = new HashMap<Short, AiEntity>();

    @Getter
    private Map<Short, StationaryEntity> stationaryEntityList = new HashMap<Short, StationaryEntity>();

    @Getter
    private Map<Short, ItemStackDrop> itemStackDropList = new HashMap<Short, ItemStackDrop>();

    public void addPlayerEntity(short entityId, Player player) {
        playerEntityList.put(entityId, player);
    }

    public void removePlayerEntity(Short entityId) {
        if (!playerEntityList.containsKey(entityId))
            println(getClass(), "Entity ID DOES NOT EXIST!", true);

        shouldClearTarget(playerEntityList.remove(entityId));
    }

    public Player getPlayerEntity(short entityId) {
        return playerEntityList.get(entityId);
    }

    public void addAiEntity(short entityId, AiEntity entity) {
        aiEntityList.put(entityId, entity);
    }

    public void removeAiEntity(Short entityId) {
        shouldClearTarget(aiEntityList.remove(entityId));
    }

    public AiEntity getAiEntity(short entityId) {
        return aiEntityList.get(entityId);
    }

    public void addStationaryEntity(short entityId, StationaryEntity entity) {
        stationaryEntityList.put(entityId, entity);
    }

    public void removeStationaryEntity(Short entityId) {
        stationaryEntityList.remove(entityId);
    }

    public StationaryEntity getStationaryEntity(short entityId) {
        return stationaryEntityList.get(entityId);
    }

    public void addItemStackDrop(short entityId, ItemStackDrop entity) {
        itemStackDropList.put(entityId, entity);
        println(getClass(), "ItemStack put into map");
    }

    public void removeItemStackDrop(Short entityId) {
        itemStackDropList.remove(entityId);
        println(getClass(), "ItemStack removed from map");
    }

    public ItemStackDrop getItemStackDrop(short entityId) {
        return itemStackDropList.get(entityId);
    }

    public void drawEntityBodies(float delta, SpriteBatch spriteBatch, PlayerClient playerClient) {
        // Draw Items on ground
        for (ItemStackDrop itemStackDrop : itemStackDropList.values()) {
            ItemStack itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack((int) itemStackDrop.getAppearance().getMonsterBodyTexture(), 1);
            spriteBatch.draw(Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEMS).findRegion(itemStack.getTextureRegion()), itemStackDrop.getDrawX() + 4, itemStackDrop.getDrawY() + 4, 8, 8);
        }
        // Draw over items
        for (StationaryEntity stationaryEntity : stationaryEntityList.values()) {
            spriteBatch.draw(Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.SKILL_NODES).findRegion("ore_00_0" + stationaryEntity.getAppearance().getMonsterBodyTexture()), stationaryEntity.getDrawX(), stationaryEntity.getDrawY());
        }

        PriorityQueue<MovingEntity> yAxisSortedEntities = new PriorityQueue<MovingEntity>();

        // Draw moving entities over items and stationary entities
        for (AiEntity movingEntity : aiEntityList.values()) {
            yAxisSortedEntities.add(movingEntity);
        }

        // Draw player entities over items and stationary entities
        for (Player player : playerEntityList.values()) {
            yAxisSortedEntities.add(player);
        }
        yAxisSortedEntities.add(playerClient);
        while (!yAxisSortedEntities.isEmpty()) {
            MovingEntity movingEntity = yAxisSortedEntities.poll();
            movingEntity.getEntityAnimation().animate(delta, spriteBatch);
        }
    }

    public void drawEntityNames() {
        for (MovingEntity movingEntity : aiEntityList.values()) {
            movingEntity.drawEntityName();
        }
        for (Player player : playerEntityList.values()) {
            player.drawEntityName();
        }
    }

    public void drawDamageNumbers() {
        for (MovingEntity movingEntity : aiEntityList.values()) {
            movingEntity.drawFloatingNumbers();
        }
        for (Player player : playerEntityList.values()) {
            player.drawFloatingNumbers();
        }
    }

    public void drawHealthBar() {
        for (MovingEntity movingEntity : aiEntityList.values()) {
            movingEntity.drawEntityHpBar();
        }
        for (Player player : playerEntityList.values()) {
            player.drawEntityHpBar();
        }
    }

    private void shouldClearTarget(MovingEntity movingEntity) {
        if (movingEntity == null) return;
        MovingEntity targetEntity = playerClient.getTargetEntity();

        if (targetEntity == null) return;
        if (targetEntity.getEntityType() != movingEntity.getEntityType()) return;
        if (targetEntity.getServerEntityID() == movingEntity.getServerEntityID()) {
            playerClient.setTargetEntity(null);
        }
    }

    @Override
    public void dispose() {
        playerEntityList.clear();
        aiEntityList.clear();
        stationaryEntityList.clear();
        itemStackDropList.clear();
        playerClient = null;
    }
}
