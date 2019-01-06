package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class EntityManager implements Disposable {

    private EntityManager() {
    }

    // TODO: REMOVE TEST CLIENT! Add the correct way.
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

    public void drawEntityBodies(float delta, SpriteBatch spriteBatch) {
        for (MovingEntity movingEntity : movingEntityList.values()) {
            movingEntity.getEntityAnimation().animate(delta, spriteBatch);
        }
        for (StationaryEntity stationaryEntity : stationaryEntityList.values()) {
            // ITEM, SKILL_NODE, ETC (non moving shit)
//            spriteBatch.setColor(ColorList.values()[stationaryEntity.getAppearance().getColorId()].getColor());
            spriteBatch.draw(Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEMS).findRegion("ore_01"), stationaryEntity.getDrawX(), stationaryEntity.getDrawY());
//            spriteBatch.setColor(Color.WHITE);
        }
    }

    public void drawEntityNames() {
        for (Entity entity : movingEntityList.values()) {
            if (entity instanceof MovingEntity) ((MovingEntity) entity).drawEntityName();
        }
    }

    @Override
    public void dispose() {
        movingEntityList.clear();
        stationaryEntityList.clear();
        playerClient = null;
    }
}
