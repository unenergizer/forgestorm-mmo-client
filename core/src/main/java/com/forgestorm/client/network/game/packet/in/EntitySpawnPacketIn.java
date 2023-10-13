package com.forgestorm.client.network.game.packet.in;

import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.EntityAlignment;
import com.forgestorm.client.game.screens.AttachableCamera;
import com.forgestorm.client.game.world.entities.*;
import com.forgestorm.client.game.world.entities.animations.MonsterAnimation;
import com.forgestorm.client.game.world.entities.animations.human.HumanAnimation;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.game.world.entities.FirstInteraction;
import com.forgestorm.shared.game.world.maps.MoveDirection;
import com.forgestorm.shared.io.type.GameAtlas;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_SPAWN)
public class EntitySpawnPacketIn implements PacketListener<EntitySpawnPacketIn.EntitySpawnPacket> {

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public EntitySpawnPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        final short entityId = clientHandler.readShort();
        final EntityType entityType = EntityType.getEntityType(clientHandler.readByte());
        final EntitySpawnPacket entitySpawnPacket = new EntitySpawnPacket(entityId, entityType);

        entitySpawnPacket.setEntityName(clientHandler.readString());
        entitySpawnPacket.setTileX(clientHandler.readInt());
        entitySpawnPacket.setTileY(clientHandler.readInt());
        entitySpawnPacket.setWorldZ(clientHandler.readShort());

        switch (entityType) {
            case SKILL_NODE:
                entitySpawnPacket.setBodyTexture(clientHandler.readByte());
                break;
            case ITEM_STACK:
                if (clientMain.isAdmin() || clientMain.isContentDeveloper()) {
                    entitySpawnPacket.setSpawnedFromDropTable(clientHandler.readBoolean());
                    entitySpawnPacket.setItemStackId(clientHandler.readInt());
                    entitySpawnPacket.setStackSize(clientHandler.readInt());
                    entitySpawnPacket.setRespawnTimeMin(clientHandler.readInt());
                    entitySpawnPacket.setRespawnTimeMax(clientHandler.readInt());
                }
                entitySpawnPacket.setBodyTexture(clientHandler.readByte());
                break;
            case MONSTER:
                if (clientMain.isAdmin() || clientMain.isContentDeveloper()) {
                    println(getClass(), "Reading in extra MONSTER data.", false, (clientMain.isAdmin() && PRINT_DEBUG) || (clientMain.isContentDeveloper() && PRINT_DEBUG));
                    entitySpawnPacket.setDamage(clientHandler.readInt());
                    entitySpawnPacket.setExpDrop(clientHandler.readInt());
                    entitySpawnPacket.setDropTable(clientHandler.readInt());
                    entitySpawnPacket.setProbWalkStill(clientHandler.readFloat());
                    entitySpawnPacket.setProbWalkStart(clientHandler.readFloat());
                    String worldName = clientHandler.readString();
                    int x = clientHandler.readInt();
                    int y = clientHandler.readInt();
                    short worldZ = clientHandler.readShort();
                    entitySpawnPacket.setDefaultSpawnLocation(new Location(clientMain, worldName, x, y, worldZ));
                }

                entitySpawnPacket.setFirstInteraction(FirstInteraction.getFirstInteraction(clientHandler.readByte()));
                entitySpawnPacket.setShopID(clientHandler.readShort());

                entitySpawnPacket.setEntityAlignment(EntityAlignment.getEntityAlignment(clientHandler.readByte()));

                entitySpawnPacket.setMoveDirection(MoveDirection.getDirection(clientHandler.readByte()));
                entitySpawnPacket.setMoveSpeed(clientHandler.readFloat());
                entitySpawnPacket.setMaxHealth(clientHandler.readInt());
                entitySpawnPacket.setCurrentHealth(clientHandler.readInt());

                // Appearance
                entitySpawnPacket.setBodyTexture(clientHandler.readByte());
                break;
            case NPC:
                if (clientMain.isAdmin() || clientMain.isContentDeveloper()) {
                    println(getClass(), "Reading in extra NPC data.", false, (clientMain.isAdmin() && PRINT_DEBUG) || (clientMain.isContentDeveloper() && PRINT_DEBUG));
                    entitySpawnPacket.setDamage(clientHandler.readInt());
                    entitySpawnPacket.setExpDrop(clientHandler.readInt());
                    entitySpawnPacket.setDropTable(clientHandler.readInt());
                    entitySpawnPacket.setProbWalkStill(clientHandler.readFloat());
                    entitySpawnPacket.setProbWalkStart(clientHandler.readFloat());
                    String worldName = clientHandler.readString();
                    int x = clientHandler.readInt();
                    int y = clientHandler.readInt();
                    short worldZ = clientHandler.readShort();
                    entitySpawnPacket.setDefaultSpawnLocation(new Location(clientMain, worldName, x, y, worldZ));
                }

                entitySpawnPacket.setFirstInteraction(FirstInteraction.getFirstInteraction(clientHandler.readByte()));
                entitySpawnPacket.setShopID(clientHandler.readShort());
                entitySpawnPacket.setBankKeeper(clientHandler.readBoolean());

                entitySpawnPacket.setEntityAlignment(EntityAlignment.getEntityAlignment(clientHandler.readByte()));
                entitySpawnPacket.setEntityFaction(clientHandler.readByte());

                entitySpawnPacket.setMoveDirection(MoveDirection.getDirection(clientHandler.readByte()));
                entitySpawnPacket.setMoveSpeed(clientHandler.readFloat());
                entitySpawnPacket.setMaxHealth(clientHandler.readInt());
                entitySpawnPacket.setCurrentHealth(clientHandler.readInt());

                // Appearance
                entitySpawnPacket.setHairTexture(clientHandler.readByte());
                entitySpawnPacket.setHelmTexture(clientHandler.readByte());
                entitySpawnPacket.setChestTexture(clientHandler.readByte());
                entitySpawnPacket.setPantsTexture(clientHandler.readByte());
                entitySpawnPacket.setShoesTexture(clientHandler.readByte());
                entitySpawnPacket.setHairColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setEyeColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setSkinColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setGlovesColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setLeftHandTexture(clientHandler.readByte());
                entitySpawnPacket.setRightHandTexture(clientHandler.readByte());
                entitySpawnPacket.setScriptId(clientHandler.readInt());
                break;
            case CLIENT_PLAYER:
            case PLAYER:
                entitySpawnPacket.setMoveDirection(MoveDirection.getDirection(clientHandler.readByte()));
                entitySpawnPacket.setMoveSpeed(clientHandler.readFloat());
                entitySpawnPacket.setMaxHealth(clientHandler.readInt());
                entitySpawnPacket.setCurrentHealth(clientHandler.readInt());

                // Appearance
                entitySpawnPacket.setHairTexture(clientHandler.readByte());
                entitySpawnPacket.setHelmTexture(clientHandler.readByte());
                entitySpawnPacket.setChestTexture(clientHandler.readByte());
                entitySpawnPacket.setPantsTexture(clientHandler.readByte());
                entitySpawnPacket.setShoesTexture(clientHandler.readByte());
                entitySpawnPacket.setHairColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setEyeColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setSkinColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setGlovesColor(new Color(clientHandler.readInt()));
                entitySpawnPacket.setLeftHandTexture(clientHandler.readByte());
                entitySpawnPacket.setRightHandTexture(clientHandler.readByte());
                break;
        }

        return entitySpawnPacket;
    }

    @Override
    public void onEvent(EntitySpawnPacket packetData) {
        String worldName = clientMain.getWorldManager().getCurrentGameWorld().getWorldName();
        Entity entity = null;
        if (packetData.entityType == EntityType.CLIENT_PLAYER) {
            entity = spawnClientPlayer(packetData, worldName);
        } else if (packetData.entityType == EntityType.PLAYER) {
            entity = spawnPlayer(packetData, worldName);
        } else if (packetData.entityType == EntityType.NPC) {
            entity = spawnNPC(packetData, worldName);
        } else if (packetData.entityType == EntityType.ITEM_STACK) {
            entity = spawnItemStackDrop(packetData);
        } else if (packetData.entityType == EntityType.MONSTER) {
            entity = spawnMonster(packetData, worldName);
        } else if (packetData.entityType == EntityType.SKILL_NODE) {
            entity = spawnSkillNode(packetData);
        }

        entity.setEntityType(packetData.entityType);
        entity.setServerEntityID(packetData.entityId);
        entity.setEntityName(packetData.entityName);
        entity.setWorldName(worldName);
        entity.setCurrentMapLocation(new Location(clientMain, entity.getWorldName(), packetData.tileX, packetData.tileY, packetData.worldZ));
        entity.setDrawX(packetData.tileX * ClientConstants.TILE_SIZE);
        entity.setDrawY(packetData.tileY * ClientConstants.TILE_SIZE);

        println(getClass(), "===[ SPAWN IN ]================================", false, PRINT_DEBUG);
        println(getClass(), "entityType: " + packetData.entityType, false, PRINT_DEBUG);
        println(getClass(), "entityId: " + packetData.entityId, false, PRINT_DEBUG);
        println(getClass(), "entityName: " + packetData.entityName, false, PRINT_DEBUG);
        println(getClass(), "worldName: NULL?", false, PRINT_DEBUG);
        println(getClass(), "tileX: " + packetData.tileX, false, PRINT_DEBUG);
        println(getClass(), "tileY: " + packetData.tileY, false, PRINT_DEBUG);
        println(getClass(), "worldZ: " + packetData.worldZ, false, PRINT_DEBUG);
        println(getClass(), "directional Byte: " + packetData.moveDirection, false, PRINT_DEBUG);
        println(getClass(), "move speed: " + packetData.moveSpeed, false, PRINT_DEBUG);
        println(getClass(), "MaxHP: " + packetData.maxHealth, false, PRINT_DEBUG);
        println(getClass(), "CurrentHp: " + packetData.currentHealth, false, PRINT_DEBUG);

        Appearance appearance = new Appearance();
        entity.setAppearance(appearance);

        // This is for setting animation data.
        switch (packetData.entityType) {
            case CLIENT_PLAYER:
            case PLAYER:
                appearance.setHairTexture(packetData.hairTexture);
                appearance.setHelmTexture(packetData.helmTexture);
                appearance.setChestTexture(packetData.chestTexture);
                appearance.setPantsTexture(packetData.pantsTexture);
                appearance.setShoesTexture(packetData.shoesTexture);
                appearance.setHairColor(packetData.hairColor);
                appearance.setEyeColor(packetData.eyeColor);
                appearance.setSkinColor(packetData.skinColor);
                appearance.setGlovesColor(packetData.glovesColor);
                appearance.setLeftHandTexture(packetData.leftHandTexture);
                appearance.setRightHandTexture(packetData.rightHandTexture);

                MovingEntity humanEntity = (MovingEntity) entity;
                humanEntity.setEntityAnimation(new HumanAnimation(humanEntity));
                humanEntity.loadTextures(GameAtlas.ENTITY_CHARACTER);

                // Process region stuff. Play sounds, music, etc...
                if (packetData.entityType == EntityType.CLIENT_PLAYER) {
                    clientMain.getRegionManager().playerEnterLocation(humanEntity.getCurrentMapLocation());
                }

                if (packetData.entityType == EntityType.CLIENT_PLAYER) {
                    clientMain.getStageHandler().getEquipmentWindow().rebuildPreviewTable();
                    // TODO: Possible to relocate this for better performance...

                    // Load the map for the player
                    clientMain.getWorldManager().getCurrentGameWorld().loadAroundPlayer((PlayerClient) entity);
                }

                println(getClass(), "Hair: " + appearance.getHairTexture(), false, PRINT_DEBUG);
                println(getClass(), "Helm: " + appearance.getHelmTexture(), false, PRINT_DEBUG);
                println(getClass(), "Chest: " + appearance.getChestTexture(), false, PRINT_DEBUG);
                println(getClass(), "Pants: " + appearance.getPantsTexture(), false, PRINT_DEBUG);
                println(getClass(), "Shoes: " + appearance.getShoesTexture(), false, PRINT_DEBUG);
                println(getClass(), "HairColor: " + appearance.getHairColor(), false, PRINT_DEBUG);
                println(getClass(), "EyeColor: " + appearance.getEyeColor(), false, PRINT_DEBUG);
                println(getClass(), "SkinColor: " + appearance.getSkinColor(), false, PRINT_DEBUG);
                println(getClass(), "GlovesColor: " + appearance.getGlovesColor(), false, PRINT_DEBUG);
                println(getClass(), "LeftHand: " + appearance.getLeftHandTexture(), false, PRINT_DEBUG);
                println(getClass(), "RightHand: " + appearance.getRightHandTexture(), false, PRINT_DEBUG);

                break;
            case NPC:
                if (clientMain.isAdmin() || clientMain.isContentDeveloper()) {
                    NPC npc = (NPC) entity;
                    npc.setDamage(packetData.damage);
                    npc.setExpDrop(packetData.expDrop);
                    npc.setDropTable(packetData.dropTable);
                    npc.setProbWalkStill(packetData.probWalkStill);
                    npc.setProbWalkStart(packetData.probWalkStart);
                    npc.setDefaultSpawnLocation(packetData.defaultSpawnLocation);
                    npc.setScriptId(packetData.scriptId);
                }

                appearance.setHairTexture(packetData.hairTexture);
                appearance.setHelmTexture(packetData.helmTexture);
                appearance.setChestTexture(packetData.chestTexture);
                appearance.setPantsTexture(packetData.pantsTexture);
                appearance.setShoesTexture(packetData.shoesTexture);
                appearance.setHairColor(packetData.hairColor);
                appearance.setEyeColor(packetData.eyeColor);
                appearance.setSkinColor(packetData.skinColor);
                appearance.setGlovesColor(packetData.glovesColor);
                appearance.setLeftHandTexture(packetData.leftHandTexture);
                appearance.setRightHandTexture(packetData.rightHandTexture);

                MovingEntity npcEntity = (MovingEntity) entity;
                npcEntity.setEntityAnimation(new HumanAnimation(npcEntity));
                npcEntity.loadTextures(GameAtlas.ENTITY_CHARACTER);

                println(getClass(), "Hair: " + appearance.getHairTexture(), false, PRINT_DEBUG);
                println(getClass(), "Helm: " + appearance.getHelmTexture(), false, PRINT_DEBUG);
                println(getClass(), "Chest: " + appearance.getChestTexture(), false, PRINT_DEBUG);
                println(getClass(), "Pants: " + appearance.getPantsTexture(), false, PRINT_DEBUG);
                println(getClass(), "Shoes: " + appearance.getShoesTexture(), false, PRINT_DEBUG);
                println(getClass(), "HairColor: " + appearance.getHairColor(), false, PRINT_DEBUG);
                println(getClass(), "EyeColor: " + appearance.getEyeColor(), false, PRINT_DEBUG);
                println(getClass(), "SkinColor: " + appearance.getSkinColor(), false, PRINT_DEBUG);
                println(getClass(), "GlovesColor: " + appearance.getGlovesColor(), false, PRINT_DEBUG);
                println(getClass(), "LeftHand: " + appearance.getLeftHandTexture(), false, PRINT_DEBUG);
                println(getClass(), "RightHand: " + appearance.getRightHandTexture(), false, PRINT_DEBUG);
                break;
            case MONSTER:
                if (clientMain.isAdmin() || clientMain.isContentDeveloper()) {
                    Monster monster = (Monster) entity;
                    monster.setDamage(packetData.damage);
                    monster.setExpDrop(packetData.expDrop);
                    monster.setDropTable(packetData.dropTable);
                    monster.setProbWalkStill(packetData.probWalkStill);
                    monster.setProbWalkStart(packetData.probWalkStart);
                    monster.setDefaultSpawnLocation(packetData.defaultSpawnLocation);
                }

                appearance.setSingleBodyTexture(packetData.bodyTexture);

                MovingEntity monsterEntity = (MovingEntity) entity;
                monsterEntity.setEntityAnimation(new MonsterAnimation(monsterEntity));
                monsterEntity.loadTextures(GameAtlas.ENTITY_MONSTER);
                break;
            case SKILL_NODE:
                appearance.setSingleBodyTexture(packetData.bodyTexture);
                break;
            case ITEM_STACK:
                if (clientMain.isAdmin() || clientMain.isContentDeveloper()) {
                    ItemStackDrop itemStackDrop = (ItemStackDrop) entity;
                    itemStackDrop.setSpawnedFromDropTable(packetData.spawnedFromDropTable);
                    itemStackDrop.setItemStackId(packetData.itemStackId);
                    itemStackDrop.setStackSize(packetData.stackSize);
                    itemStackDrop.setRespawnTimeMin(packetData.respawnTimeMin);
                    itemStackDrop.setRespawnTimeMax(packetData.respawnTimeMax);
                }
                appearance.setSingleBodyTexture(packetData.bodyTexture);
                break;
        }
    }

    private Entity spawnClientPlayer(EntitySpawnPacket packetData, String worldName) {
        PlayerClient playerClient = new PlayerClient(clientMain);
        AttachableCamera camera = clientMain.getGameScreen().getCamera();

        // Attach entity to camera
        camera.attachEntity(playerClient);

        clientMain.getGameScreen().getKeyboard().getKeyboardMovement().setInvalidated(false);
        clientMain.getMouseManager().setInvalidate(false);

        setMovingEntityVars(playerClient, packetData, worldName);

        clientMain.getEntityManager().setPlayerClient(playerClient);

        clientMain.getStageHandler().getStatusBar().initHealth(packetData.currentHealth, packetData.maxHealth);

        return playerClient;
    }

    private Entity spawnPlayer(EntitySpawnPacket packetData, String worldName) {
        Player player = new Player(clientMain);
        setMovingEntityVars(player, packetData, worldName);
        clientMain.getEntityManager().addPlayerEntity(packetData.entityId, player);
        return player;
    }

    private Entity spawnMonster(EntitySpawnPacket packetData, String worldName) {
        AiEntity entity = new Monster(clientMain);
        entity.setFirstInteraction(packetData.firstInteraction);
        entity.setShopID(packetData.shopID);
        entity.setAlignment(packetData.entityAlignment);
        setMovingEntityVars(entity, packetData, worldName);
        clientMain.getEntityManager().addAiEntity(packetData.entityId, entity);
        return entity;
    }

    private Entity spawnNPC(EntitySpawnPacket packetData, String worldName) {
        NPC npc = new NPC(clientMain);
        npc.setFirstInteraction(packetData.firstInteraction);
        npc.setShopID(packetData.shopID);
        npc.setBankKeeper(packetData.isBankKeeper);
        npc.setAlignment(packetData.entityAlignment);
        npc.setFaction(packetData.entityFaction);
        setMovingEntityVars(npc, packetData, worldName);
        clientMain.getEntityManager().addAiEntity(packetData.entityId, npc);
        return npc;
    }

    private Entity spawnSkillNode(EntitySpawnPacket packetData) {
        SkillNode skillNode = new SkillNode(clientMain);
        clientMain.getEntityManager().addStationaryEntity(packetData.entityId, skillNode);
        return skillNode;
    }

    private Entity spawnItemStackDrop(EntitySpawnPacket packetData) {
        ItemStackDrop itemStackDrop = new ItemStackDrop(clientMain);
        clientMain.getEntityManager().addItemStackDrop(packetData.entityId, itemStackDrop);
        return itemStackDrop;
    }

    private void setMovingEntityVars(MovingEntity entity, EntitySpawnPacket packetData, String worldName) {
        entity.setFutureMapLocation(new Location(clientMain, worldName, packetData.tileX, packetData.tileY, packetData.worldZ));
        MoveDirection facingDirection = packetData.moveDirection;

        if (facingDirection == MoveDirection.NONE) {
            throw new RuntimeException("The server sent a facing direction of NONE for some reason.");
        }

        entity.setFacingDirection(facingDirection);
        entity.setMoveSpeed(packetData.moveSpeed);

        // setup health
        entity.setMaxHealth(packetData.maxHealth);
        entity.setCurrentHealth(packetData.currentHealth);
    }

    @Setter
    static class EntitySpawnPacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;

        // Admin data AiEntity
        private int damage;
        private int expDrop;
        private int dropTable;
        private float probWalkStill;
        private float probWalkStart;
        private Location defaultSpawnLocation;

        // Admin data ItemStackDrop
        private boolean spawnedFromDropTable;
        private int itemStackId;
        private int stackSize;
        private int respawnTimeMin;
        private int respawnTimeMax;

        // Base data
        private String entityName;
        private int tileX;
        private int tileY;
        private short worldZ;
        private MoveDirection moveDirection;
        private float moveSpeed;
        private int maxHealth;
        private int currentHealth;
        private EntityAlignment entityAlignment;
        private byte entityFaction;
        private short shopID;
        private boolean isBankKeeper;
        private FirstInteraction firstInteraction;

        // Appearance data
        private byte bodyTexture;
        private byte hairTexture;
        private byte helmTexture;
        private byte chestTexture;
        private byte pantsTexture;
        private byte shoesTexture;
        private Color hairColor;
        private Color eyeColor;
        private Color skinColor;
        private Color glovesColor;
        private byte leftHandTexture;
        private byte rightHandTexture;

        // Scripting
        private int scriptId;

        EntitySpawnPacket(short entityId, EntityType entityType) {
            this.entityId = entityId;
            this.entityType = entityType;
        }
    }
}
