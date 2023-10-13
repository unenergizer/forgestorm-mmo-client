package com.forgestorm.client.game.world.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.settings.GameMechanicsTab;
import com.forgestorm.client.game.world.WorldObject;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.io.type.GameAtlas;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static com.forgestorm.client.util.Log.println;

public class EntityManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;

    @Getter
    @Setter
    private PlayerClient playerClient;

    //  EntityId -> Entity
    @Getter
    private final Map<Short, Player> playerEntityList = new HashMap<>();

    @Getter
    private final Map<Short, AiEntity> aiEntityList = new HashMap<>();

    @Getter
    private final Map<Short, StationaryEntity> stationaryEntityList = new HashMap<>();

    @Getter
    private final Map<Short, ItemStackDrop> itemStackDropList = new HashMap<>();

    public EntityManager(ClientMain clientMain) {
        this.clientMain = clientMain;
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

    public void getSortableEntities(PriorityQueue<WorldObject> worldObjectList) {
        worldObjectList.addAll(itemStackDropList.values());
        worldObjectList.addAll(aiEntityList.values());
        worldObjectList.addAll(playerEntityList.values());
    }

    public void drawGroundEntities(SpriteBatch spriteBatch) {
        // Draw Items on ground
//        for (ItemStackDrop itemStackDrop : itemStackDropList.values()) {
//            ItemStack itemStack = clientMain.getItemStackManager().makeItemStack(itemStackDrop.getAppearance().getSingleBodyTexture(), 1);
//            spriteBatch.draw(clientMain.getFileManager().getAtlas(GameAtlas.ITEMS).findRegion(itemStack.getTextureRegion()), itemStackDrop.getDrawX() + 4, itemStackDrop.getDrawY() + 4, 8, 8);
//        }
        // Draw over items
        for (StationaryEntity stationaryEntity : stationaryEntityList.values()) {
            spriteBatch.draw(clientMain.getFileManager().getAtlas(GameAtlas.SKILL_NODES).findRegion("ore_00_0" + stationaryEntity.getAppearance().getSingleBodyTexture()), stationaryEntity.getDrawX(), stationaryEntity.getDrawY());
        }
    }

    public void drawEntityShadows(SpriteBatch spriteBatch, Floors floor) {
        // Draw shadows underneath aiEntities
        for (AiEntity aiEntity : aiEntityList.values()) {
            if (aiEntity.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
            aiEntity.drawShadow(spriteBatch);
        }

        // Draw shadows underneath players
        for (Player player : playerEntityList.values()) {
            if (player.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
            player.drawShadow(spriteBatch);
        }

        // Draw the player shadow
        if (playerClient.getCurrentMapLocation().getZ() == floor.getWorldZ()) {
            playerClient.drawShadow(spriteBatch);
        }
    }

    public void drawEntityNames(Floors floor) {
        GameMechanicsTab gameMechanicsTab = clientMain.getStageHandler().getMainSettingsWindow().getGameMechanicsTab();
        if (gameMechanicsTab.getEntityNameVisibleCheckBox().isChecked()) {
            for (MovingEntity movingEntity : aiEntityList.values()) {
                if (movingEntity.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
                movingEntity.drawEntityName();
            }
        }
        if (gameMechanicsTab.getOtherPlayerNameVisibleCheckBox().isChecked()) {
            for (Player player : playerEntityList.values()) {
                if (player.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
                player.drawEntityName();
            }
        }
    }

    public void drawDamageNumbers(Floors floor) {
        for (MovingEntity movingEntity : aiEntityList.values()) {
            if (movingEntity.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
            movingEntity.drawFloatingNumbers();
        }
        for (Player player : playerEntityList.values()) {
            if (player.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
            player.drawFloatingNumbers();
        }
    }

    public void drawHealthBar(Floors floor) {
        GameMechanicsTab gameMechanicsTab = clientMain.getStageHandler().getMainSettingsWindow().getGameMechanicsTab();
        if (gameMechanicsTab.getEntityHealthBarVisibleCheckBox().isChecked()) {
            for (MovingEntity movingEntity : aiEntityList.values()) {
                if (movingEntity.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
                movingEntity.drawEntityHpBar();
            }
        }
        if (gameMechanicsTab.getOtherPlayerHealthBarVisibleCheckBox().isChecked()) {
            for (Player player : playerEntityList.values()) {
                if (player.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
                player.drawEntityHpBar();
            }
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
