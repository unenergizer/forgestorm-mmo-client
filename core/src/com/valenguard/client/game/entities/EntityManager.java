package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

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
        playerEntityList.remove(entityId);
    }

    public Player getPlayerEntity(short entityId) {
        return playerEntityList.get(entityId);
    }

    public void addAiEntity(short entityId, AiEntity entity) {
        aiEntityList.put(entityId, entity);
    }

    public void removeAiEntity(Short entityId) {
        aiEntityList.remove(entityId);
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

    public void drawEntityBodies(float delta, SpriteBatch spriteBatch) {
        // Draw Items on ground
        for (ItemStackDrop itemStackDrop : itemStackDropList.values()) {
            ItemStack itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack((int) itemStackDrop.getAppearance().getTextureId(0), 1);
            spriteBatch.draw(Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEMS).findRegion(itemStack.getTextureRegion()), itemStackDrop.getDrawX() + 4, itemStackDrop.getDrawY() + 4, 8, 8);
        }
        // Draw over items
        for (StationaryEntity stationaryEntity : stationaryEntityList.values()) {
            spriteBatch.draw(Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.SKILL_NODES).findRegion("ore_00_0" + stationaryEntity.getAppearance().getTextureId(0)), stationaryEntity.getDrawX(), stationaryEntity.getDrawY());
        }
        // Draw moving entities over items and stationary entities
        for (MovingEntity movingEntity : aiEntityList.values()) {
            movingEntity.getEntityAnimation().animate(delta, spriteBatch);
        }
        // Draw player entities over items and stationary entities
        for (Player player : playerEntityList.values()) {
            player.getEntityAnimation().animate(delta, spriteBatch);
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

    @Override
    public void dispose() {
        playerEntityList.clear();
        aiEntityList.clear();
        stationaryEntityList.clear();
        itemStackDropList.clear();
        playerClient = null;
    }
}
