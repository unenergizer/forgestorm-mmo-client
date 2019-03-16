package com.valenguard.client.network.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.AiEntity;
import com.valenguard.client.game.entities.Appearance;
import com.valenguard.client.game.entities.Entity;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.ItemStackDrop;
import com.valenguard.client.game.entities.Monster;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.NPC;
import com.valenguard.client.game.entities.Player;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.entities.SkillNode;
import com.valenguard.client.game.entities.StationaryEntity;
import com.valenguard.client.game.entities.animations.HumanAnimation;
import com.valenguard.client.game.entities.animations.MonsterAnimation;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.rpg.EntityAlignment;
import com.valenguard.client.game.rpg.FactionTypes;
import com.valenguard.client.game.screens.AttachableCamera;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;
import com.valenguard.client.util.ColorList;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;
import static com.valenguard.client.util.Preconditions.checkNotNull;

@Opcode(getOpcode = Opcodes.ENTITY_SPAWN)
public class EntitySpawnPacketIn implements PacketListener<EntitySpawnPacketIn.EntitySpawnPacket> {

    private static final boolean PRINT_DEBUG = true;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        final EntityType entityType = EntityType.getEntityType(clientHandler.readByte());
        final short entityId = clientHandler.readShort(); // TODO: this will be different later depending on the entity type
        final String entityName = clientHandler.readString();
        final short tileX = clientHandler.readShort();
        final short tileY = clientHandler.readShort();
        byte directionalByte = 0;
        float moveSpeed = 0.0f;
        short[] textureIds = null;
        byte colorId = 0;
        int maxHealth = 0;
        int currentHealth = 0;
        EntityAlignment entityAlignment = null;
        FactionTypes entityFaction = null;
        short shopID = -1;

        checkNotNull(entityType, "EntityType can not be null!");

        switch (entityType) {
            case SKILL_NODE:
            case ITEM_STACK:
                textureIds = new short[1];
                textureIds[Appearance.BODY] = clientHandler.readShort();
                break;
            case MONSTER:
                textureIds = new short[1];
                textureIds[Appearance.BODY] = clientHandler.readShort();
                shopID = clientHandler.readShort();
                entityAlignment = EntityAlignment.getEntityAlignment(clientHandler.readByte());
                break;
            case NPC:
                colorId = clientHandler.readByte();
                textureIds = new short[2];
                textureIds[Appearance.BODY] = clientHandler.readShort();
                textureIds[Appearance.HEAD] = clientHandler.readShort();
                shopID = clientHandler.readShort();
                entityAlignment = EntityAlignment.getEntityAlignment(clientHandler.readByte());
                entityFaction = FactionTypes.getFactionType(clientHandler.readByte());
                break;
            case CLIENT_PLAYER:
            case PLAYER:
                colorId = clientHandler.readByte();
                textureIds = new short[4];
                textureIds[Appearance.BODY] = clientHandler.readShort();
                textureIds[Appearance.HEAD] = clientHandler.readShort();
                textureIds[Appearance.ARMOR] = clientHandler.readShort();
                textureIds[Appearance.HELM] = clientHandler.readShort();
                break;
        }


        if (entityType != EntityType.ITEM_STACK && entityType != EntityType.SKILL_NODE) {
            directionalByte = clientHandler.readByte();
            moveSpeed = clientHandler.readFloat();
            maxHealth = clientHandler.readInt();
            currentHealth = clientHandler.readInt();
        }

        println(getClass(), "===================================", false, PRINT_DEBUG);
        println(getClass(), "entityType: " + entityType, false, PRINT_DEBUG);
        println(getClass(), "entityId: " + entityId, false, PRINT_DEBUG);
        println(getClass(), "entityName: " + entityName, false, PRINT_DEBUG);
        println(getClass(), "tileX: " + tileX, false, PRINT_DEBUG);
        println(getClass(), "tileY: " + tileY, false, PRINT_DEBUG);
        println(getClass(), "directional Byte: " + directionalByte, false, PRINT_DEBUG);
        println(getClass(), "move speed: " + moveSpeed, false, PRINT_DEBUG);
        println(getClass(), "Color ID: " + colorId, false, PRINT_DEBUG);
        for (int i = 0; i < textureIds.length; i++) {
            println(getClass(), "textureIds #" + i + ": " + textureIds[i], false, PRINT_DEBUG);
        }

        return new EntitySpawnPacket(
                entityType,
                entityId,
                entityName,
                tileX,
                tileY,
                textureIds,
                colorId,
                directionalByte,
                moveSpeed,
                maxHealth,
                currentHealth,
                entityAlignment,
                entityFaction,
                shopID
        );
    }

    @Override
    public void onEvent(EntitySpawnPacket packetData) {
        String mapName = Valenguard.gameScreen.getMapRenderer().getGameMapNameFromServer();
        Entity entity = null;
        if (packetData.entityType == EntityType.CLIENT_PLAYER) {
            entity = spawnClientPlayer(packetData);
        } else if (packetData.entityType == EntityType.PLAYER) {
            entity = spawnPlayer(packetData);
        } else if (packetData.entityType == EntityType.NPC) {
            entity = spawnNPC(packetData);
        } else if (packetData.entityType == EntityType.ITEM_STACK) {
            entity = spawnItem(packetData);
        } else if (packetData.entityType == EntityType.MONSTER) {
            entity = spawnMonster(packetData);
        } else if (packetData.entityType == EntityType.SKILL_NODE) {
            entity = spawnSkillNode(packetData);
        }

        //noinspection ConstantConditions
        entity.setEntityType(packetData.entityType);
        entity.setServerEntityID(packetData.entityId);
        entity.setEntityName(packetData.entityName);
        entity.setMapName(mapName);
        entity.setCurrentMapLocation(new Location(entity.getMapName(), packetData.tileX, packetData.tileY));
        entity.setDrawX(packetData.tileX * ClientConstants.TILE_SIZE);
        entity.setDrawY(packetData.tileY * ClientConstants.TILE_SIZE);
        entity.setAppearance(new Appearance(ColorList.getColorList(packetData.colorId).getColor(), packetData.textureIds));

        // This is for setting animation data.
        switch (packetData.entityType) {
            case CLIENT_PLAYER:
            case NPC:
            case PLAYER:
                MovingEntity humanEntity = (MovingEntity) entity;
                humanEntity.setEntityAnimation(new HumanAnimation(humanEntity));
                humanEntity.loadTextures(GameAtlas.ENTITY_CHARACTER);
                break;
            case MONSTER:
                MovingEntity monsterEntity = (MovingEntity) entity;
                monsterEntity.setEntityAnimation(new MonsterAnimation(monsterEntity));
                monsterEntity.loadTextures(GameAtlas.ENTITY_MONSTER);
                break;
            case SKILL_NODE:
            case ITEM_STACK:
                // TODO: Implement or ignore entity type for animation
                break;
        }
    }

    private Entity spawnClientPlayer(EntitySpawnPacket packetData) {
        Entity entity = new PlayerClient();

        PlayerClient playerClient = (PlayerClient) entity;
        AttachableCamera camera = Valenguard.gameScreen.getCamera();
        println(EntitySpawnPacketIn.class, "Found player. Initializing the player", false, PRINT_DEBUG);

        // Attach entity to camera
        camera.attachEntity(playerClient);

        Valenguard.gameScreen.getKeyboard().getKeyboardMovement().setInvalidated(false);
        Valenguard.getInstance().getMouseManager().setInvalidate(false);

        setMovingEntityVars((MovingEntity) entity, packetData);

        EntityManager.getInstance().setPlayerClient(playerClient);

        Valenguard.getInstance().getStageHandler().getStatusBar().initHealth(packetData.currentHealth, packetData.maxHealth);
        return entity;
    }

    private Entity spawnPlayer(EntitySpawnPacket packetData) {
        Entity entity = new Player();
        setMovingEntityVars((Player) entity, packetData);
        EntityManager.getInstance().addPlayerEntity(packetData.entityId, (Player) entity);
        return entity;
    }

    private Entity spawnMonster(EntitySpawnPacket packetData) {
        Entity entity = new Monster();
        ((AiEntity) entity).setShopID(packetData.shopID);
        ((AiEntity) entity).setAlignment(packetData.entityAlignment);
        setMovingEntityVars((MovingEntity) entity, packetData);
        EntityManager.getInstance().addMovingEntity(packetData.entityId, (MovingEntity) entity);
        return entity;
    }

    private Entity spawnNPC(EntitySpawnPacket packetData) {
        Entity entity = new NPC();
        ((AiEntity) entity).setShopID(packetData.shopID);
        ((AiEntity) entity).setAlignment(packetData.entityAlignment);
        ((NPC) entity).setFaction(packetData.entityFaction);
        setMovingEntityVars((MovingEntity) entity, packetData);
        EntityManager.getInstance().addMovingEntity(packetData.entityId, (MovingEntity) entity);
        return entity;
    }

    private Entity spawnSkillNode(EntitySpawnPacket packetData) {
        println(getClass(), "Spawning skill node!", false, PRINT_DEBUG);
        Entity entity = new SkillNode();
        EntityManager.getInstance().addStationaryEntity(packetData.entityId, (StationaryEntity) entity);
        return entity;
    }

    private Entity spawnItem(EntitySpawnPacket packetData) {
        Entity entity = new ItemStackDrop();
        EntityManager.getInstance().addItemStackDrop(packetData.entityId, (ItemStackDrop) entity);
        println(getClass(), "ItemStack spawn! ID: " + packetData.entityId);
        return entity;
    }

    private void setMovingEntityVars(MovingEntity entity, EntitySpawnPacket packetData) {
        entity.setFutureMapLocation(new Location(entity.getMapName(), packetData.tileX, packetData.tileY));
        MoveDirection facingDirection = MoveDirection.getDirection(packetData.facingMoveDirectionByte);

        if (facingDirection == MoveDirection.NONE) {
            throw new RuntimeException("The server sent a facing direction of NONE for some reason.");
        }

        entity.setFacingDirection(facingDirection);
        entity.setMoveSpeed(packetData.moveSpeed);

        // setup health
        entity.setMaxHealth(packetData.maxHealth);
        entity.setCurrentHealth(packetData.currentHealth);
    }

    @AllArgsConstructor
    class EntitySpawnPacket extends PacketData {
        private EntityType entityType;
        private short entityId;
        private String entityName;
        private final short tileX;
        private final short tileY;
        private final short[] textureIds;
        private final byte colorId;
        private final byte facingMoveDirectionByte;
        private final float moveSpeed;
        private final int maxHealth;
        private final int currentHealth;
        private final EntityAlignment entityAlignment;
        private final FactionTypes entityFaction;
        private final short shopID;
    }
}
