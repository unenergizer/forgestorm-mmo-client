package com.forgestorm.client.game.world.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.io.type.GameAtlas;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class EntityManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;

    @Getter
    @Setter
    private PlayerClient playerClient;

    private static EntityManager instance;

    //  EntityId -> Entity
    @Getter
    private final Map<Short, Player> playerEntityList = new HashMap<Short, Player>();

    @Getter
    private final Map<Short, AiEntity> aiEntityList = new HashMap<Short, AiEntity>();

    @Getter
    private final Map<Short, StationaryEntity> stationaryEntityList = new HashMap<Short, StationaryEntity>();

    @Getter
    private final Map<Short, ItemStackDrop> itemStackDropList = new HashMap<Short, ItemStackDrop>();

    private EntityManager() {
    }

    public static EntityManager getInstance() {
        if (instance == null) instance = new EntityManager();
        return instance;
    }

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
        println(getClass(), "ItemStack put into map", false, PRINT_DEBUG);
    }

    public void removeItemStackDrop(Short entityId) {
        itemStackDropList.remove(entityId);
        println(getClass(), "ItemStack removed from map", false, PRINT_DEBUG);
    }

    public void drawEntityShadows(SpriteBatch spriteBatch) {
        // Draw shadows underneath aiEntities
        for (AiEntity aiEntity : aiEntityList.values()) {
            aiEntity.drawShadow(spriteBatch);
        }

        // Draw shadows underneath players
        for (Player player : playerEntityList.values()) {
            player.drawShadow(spriteBatch);
        }

        // Draw the player shadow
        playerClient.drawShadow(spriteBatch);
    }

    public void drawEntities(float delta, SpriteBatch spriteBatch, PlayerClient playerClient) {
        // Draw Items on ground
        for (ItemStackDrop itemStackDrop : itemStackDropList.values()) {
            ItemStack itemStack = ClientMain.getInstance().getItemStackManager().makeItemStack(itemStackDrop.getAppearance().getSingleBodyTexture(), 1);
            spriteBatch.draw(ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.ITEMS).findRegion(itemStack.getTextureRegion()), itemStackDrop.getDrawX() + 4, itemStackDrop.getDrawY() + 4, 8, 8);
        }
        // Draw over items
        for (StationaryEntity stationaryEntity : stationaryEntityList.values()) {
            spriteBatch.draw(ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.SKILL_NODES).findRegion("ore_00_0" + stationaryEntity.getAppearance().getSingleBodyTexture()), stationaryEntity.getDrawX(), stationaryEntity.getDrawY());
        }

        // Drawing entities and players over ItemStackDrops
        // Putting both entity types into a collection to be sorted on the Y axis
        PriorityQueue<MovingEntity> yAxisSortedEntities = new PriorityQueue<MovingEntity>();
        yAxisSortedEntities.addAll(aiEntityList.values());
        yAxisSortedEntities.addAll(playerEntityList.values());
        yAxisSortedEntities.add(playerClient);

        // Now draw all the entities on the screen
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
