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
    private Map<Short, MovingEntity> movingEntityList = new HashMap<Short, MovingEntity>();

    @Getter
    private Map<Short, StationaryEntity> stationaryEntityList = new HashMap<Short, StationaryEntity>();

    @Getter
    private Map<Short, ItemStackDrop> itemStackDropList = new HashMap<Short, ItemStackDrop>();

    public void addMovingEntity(short entityId, MovingEntity entity) {
        movingEntityList.put(entityId, entity);
    }

    public void removeMovingEntity(Short entityId) {
        movingEntityList.remove(entityId);
    }

    public MovingEntity getMovingEntity(short entityId) {
        return movingEntityList.get(entityId);
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
    }

    public void removeItemStackDrop(Short entityId) {
        itemStackDropList.remove(entityId);
    }

    public ItemStackDrop getItemStackDrop(short entityId) {
        return itemStackDropList.get(entityId);
    }

    public void drawEntityBodies(float delta, SpriteBatch spriteBatch) {
        // Draw Items on ground
        for (ItemStackDrop itemStackDrop : itemStackDropList.values()) {
            ItemStack itemStack = Valenguard.getInstance().getItemManager().makeItemStack((int) itemStackDrop.getAppearance().getTextureId(0), 1);
            spriteBatch.draw(Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEMS).findRegion(itemStack.getTextureRegion()), itemStackDrop.getDrawX(), itemStackDrop.getDrawY());
        }
        // Draw over items
        for (StationaryEntity stationaryEntity : stationaryEntityList.values()) {
            spriteBatch.draw(Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.SKILL_NODES).findRegion("ore_00_0" + stationaryEntity.getAppearance().getTextureId(0)), stationaryEntity.getDrawX(), stationaryEntity.getDrawY());
        }
        // Draw moving entities over items and stationary entities
        for (MovingEntity movingEntity : movingEntityList.values()) {
            movingEntity.getEntityAnimation().animate(delta, spriteBatch);
        }
    }

    public void drawEntityNames() {
        for (MovingEntity movingEntity : movingEntityList.values()) {
            movingEntity.drawEntityName();
        }
    }

    public void drawDamageNumbers() {
        for (MovingEntity movingEntity : movingEntityList.values()) {
            movingEntity.drawFloatingNumbers();
        }
    }

    public void drawHealthBar() {
        for (MovingEntity movingEntity : movingEntityList.values()) {
            movingEntity.drawEntityHpBar();
        }
    }

    @Override
    public void dispose() {
        movingEntityList.clear();
        itemStackDropList.clear();
        playerClient = null;
    }
}
