package com.valenguard.client.network.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.Appearance;
import com.valenguard.client.game.entities.Entity;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.NPC;
import com.valenguard.client.game.entities.Player;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.entities.animations.HumanAnimation;
import com.valenguard.client.game.entities.animations.MonsterAnimation;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.Log;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ENTITY_SPAWN)
public class EntitySpawnPacketIn implements PacketListener<EntitySpawnPacketIn.EntitySpawnPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        EntityType entityType = EntityType.getEntityType(clientHandler.readByte());
        short entityId = clientHandler.readShort(); // TODO: this will be different later depending on the entity type
        String entityName = clientHandler.readString();
        int tileX = clientHandler.readInt();
        int tileY = clientHandler.readInt();
        byte directionalByte = 0;
        float moveSpeed = 0.0f;
        short[] textureIds = null;
        byte colorId = -1;

        switch (entityType) {
            case MONSTER:
            case ITEM:
                textureIds = new short[1];
                textureIds[Appearance.BODY] = clientHandler.readShort();
                break;
            case NPC:
                colorId = clientHandler.readByte();
                textureIds = new short[2];
                textureIds[Appearance.BODY] = clientHandler.readShort();
                textureIds[Appearance.HEAD] = clientHandler.readShort();
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


        if (entityType != EntityType.ITEM) {
            directionalByte = clientHandler.readByte();
            moveSpeed = clientHandler.readFloat();
        }

        Log.println(getClass(), "===================================", false, PRINT_DEBUG);
        Log.println(getClass(), "entityType: " + entityType, false, PRINT_DEBUG);
        Log.println(getClass(), "entityId: " + entityId, false, PRINT_DEBUG);
        Log.println(getClass(), "entityName: " + entityName, false, PRINT_DEBUG);
        Log.println(getClass(), "tileX: " + tileX, false, PRINT_DEBUG);
        Log.println(getClass(), "tileY: " + tileY, false, PRINT_DEBUG);
        Log.println(getClass(), "directional Byte: " + directionalByte, false, PRINT_DEBUG);
        Log.println(getClass(), "move speed: " + moveSpeed, false, PRINT_DEBUG);
        for (int i = 0; i < textureIds.length; i++) {
            Log.println(getClass(), "textureIds #" + i + ": " + textureIds[i], false, PRINT_DEBUG);
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
                moveSpeed
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
        } else if (packetData.entityType == EntityType.ITEM) {
            entity = spawnItem(packetData);
        } else if (packetData.entityType == EntityType.MONSTER) {
            entity = spawnNPC(packetData);
        }

        entity.setEntityType(packetData.entityType);
        entity.setServerEntityID(packetData.entityId);
        entity.setEntityName(packetData.entityName);
        entity.setMapName(mapName);
        entity.setCurrentMapLocation(new Location(entity.getMapName(), packetData.tileX, packetData.tileY));
        entity.setDrawX(packetData.tileX * ClientConstants.TILE_SIZE);
        entity.setDrawY(packetData.tileY * ClientConstants.TILE_SIZE);
        entity.setAppearance(new Appearance(packetData.colorId, packetData.textureIds));

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
            case ITEM: // TODO: JOSEPH COMPLAINED THIS IS NOT A MOVING ENTITY VAR... NO SHIT
//                entity.setHeadId(packetData.textureIds[0]);
                break;
        }

        // TODO REMOVE/RELOCATE
//        if (packetData.entityType == EntityType.CLIENT_PLAYER) {
//            Valenguard.getInstance().getStageHandler().getStage().addActor(Valenguard.getInstance().getStageHandler().getDebugTable().build());
//        }
    }

    private Entity spawnClientPlayer(EntitySpawnPacket packetData) {
        Entity entity = new PlayerClient();

        PlayerClient playerClient = (PlayerClient) entity;
        AttachableCamera camera = Valenguard.gameScreen.getCamera();
        Log.println(EntitySpawnPacketIn.class, "Found player. Initializing the player");

        // Attach entity to camera
        camera.attachEntity(playerClient);

        Valenguard.gameScreen.getKeyboard().getKeyboardMovement().setInvalidated(false);
        Valenguard.getInstance().getMouseManager().setInvalidate(false);

        setMovingEntityVars((MovingEntity) entity, packetData);

        EntityManager.getInstance().setPlayerClient(playerClient);
        return entity;
    }

    private Entity spawnPlayer(EntitySpawnPacket packetData) {
        Entity entity = new Player();
        setMovingEntityVars((MovingEntity) entity, packetData);
        return entity;
    }

    private Entity spawnNPC(EntitySpawnPacket packetData) {
        Entity entity = new NPC();
        setMovingEntityVars((MovingEntity) entity, packetData);
        return entity;
    }

    private Entity spawnItem(EntitySpawnPacket packetData) {
        return null;
    }

    private void setMovingEntityVars(MovingEntity entity, EntitySpawnPacket packetData) {
        entity.setFutureMapLocation(new Location(entity.getMapName(), packetData.tileX, packetData.tileY));
        MoveDirection facingDirection = MoveDirection.getDirection(packetData.facingMoveDirectionByte);

        if (facingDirection == MoveDirection.NONE) {
            throw new RuntimeException("The server sent a facing direction of NONE for some reason.");
        }

        entity.setFacingDirection(facingDirection);
        entity.setMoveSpeed(packetData.moveSpeed);

        if (!(entity instanceof PlayerClient))
            EntityManager.getInstance().addEntity(packetData.entityId, entity);
    }

    @AllArgsConstructor
    class EntitySpawnPacket extends PacketData {
        private EntityType entityType;
        private short entityId;
        private String entityName;
        private final int tileX;
        private final int tileY;
        private final short[] textureIds;
        private final byte colorId;
        private final byte facingMoveDirectionByte;
        private final float moveSpeed;
    }
}
